# Cliente WebSocket — STOMP

## Propósito

Validar movimientos drag-and-drop en tiempo real (< 100ms ida y vuelta). El frontend envía un `MovimientoDTO`, el backend evalúa los 10 HC y devuelve `ValidacionMovimientoDTO`.

## Cliente STOMP

`src/websocket/stompClient.js`:

```js
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

let client = null;
let onValidacionCallback = null;

export function conectar(onValidacion) {
  if (client?.active) return;

  onValidacionCallback = onValidacion;

  client = new Client({
    webSocketFactory: () => new SockJS('/ws'),
    reconnectDelay: 5000,
    debug: () => {},  // silenciar logs verbose

    onConnect: () => {
      client.subscribe('/topic/validacion-movimiento', (msg) => {
        try {
          const data = JSON.parse(msg.body);
          onValidacionCallback?.(data);
        } catch (e) {
          console.error('Error parseando validación:', e);
        }
      });
    },

    onStompError: (frame) => {
      console.error('STOMP error:', frame.headers.message);
    },
  });

  client.activate();
}

export function validarMovimiento(movimiento) {
  if (!client?.connected) {
    console.warn('STOMP no conectado, intentando reconectar...');
    return;
  }
  client.publish({
    destination: '/app/validar-movimiento',
    body: JSON.stringify(movimiento),
  });
}

export function desconectar() {
  client?.deactivate();
  client = null;
  onValidacionCallback = null;
}
```

## Hook useWebSocket

`src/hooks/useWebSocket.js`:

```js
import { useEffect, useCallback } from 'react';
import { conectar, validarMovimiento as wsValidar, desconectar } from '../websocket/stompClient';

export function useWebSocket(onValidacion) {
  useEffect(() => {
    conectar(onValidacion);
    return () => desconectar();
  }, [onValidacion]);

  const validarMovimiento = useCallback((movimiento) => {
    wsValidar(movimiento);
  }, []);

  return { validarMovimiento };
}
```

## Uso desde un Componente

```jsx
import { useState } from 'react';
import { useWebSocket } from '../../hooks/useWebSocket';

function MiCalendario() {
  const [validacion, setValidacion] = useState(null);

  const onValidacionRecibida = useCallback((data) => {
    setValidacion(data);  // { valido, hcViolado, mensajeError }
  }, []);

  const { validarMovimiento } = useWebSocket(onValidacionRecibida);

  const onDragOver = (asignacionId, nuevaFranjaId, nuevaAulaId) => {
    validarMovimiento({ asignacionId, nuevaFranjaId, nuevaAulaId });
  };

  return (
    <div>
      {validacion?.valido === false && (
        <div className="text-red-400">{validacion.mensajeError}</div>
      )}
      {/* ... */}
    </div>
  );
}
```

## Throttling

Para evitar enviar un mensaje WebSocket en cada `mouseover` (potencialmente 60 veces por segundo), throttlear con `lodash.throttle` o un debounce manual:

```js
import { throttle } from 'lodash';

const validarThrottled = throttle((mov) => {
  validarMovimiento(mov);
}, 100);  // máximo 10 validaciones por segundo
```

## Reconexión Automática

`reconnectDelay: 5000` en la configuración de `Client` reintenta cada 5 segundos si la conexión se pierde. La librería `@stomp/stompjs` maneja esto internamente.

## Fallback HTTP

Si el WebSocket no se puede establecer (por ejemplo, proxy corporativo bloquea), usar el endpoint REST `GET /api/asignaciones/validar-movimiento` como fallback. La latencia será mayor (~200ms vs ~50ms) pero funcionalmente equivalente.

```js
// En el componente del calendario
const validar = client?.connected
  ? validarMovimiento  // WebSocket
  : (mov) => api.get('/asignaciones/validar-movimiento', { params: mov }).then(setValidacion);
```

## Testing del WebSocket en Desarrollo

Herramienta recomendada: extension de Chrome **STOMP/WebSocket Inspector** o el script:

```js
// Conectar manualmente desde DevTools console
const sock = new SockJS('http://localhost:8080/ws');
const cli = Stomp.over(sock);
cli.connect({}, () => {
  cli.subscribe('/topic/validacion-movimiento', m => console.log(JSON.parse(m.body)));
  cli.send('/app/validar-movimiento', {}, JSON.stringify({
    asignacionId: 1, nuevaFranjaId: 5, nuevaAulaId: 2
  }));
});
```
