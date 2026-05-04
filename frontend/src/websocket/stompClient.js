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
    debug: () => {},

    onConnect: () => {
      client.subscribe('/topic/validacion-movimiento', (msg) => {
        try {
          onValidacionCallback?.(JSON.parse(msg.body));
        } catch (e) {
          console.error('Error parseando validación WS:', e);
        }
      });
    },

    onStompError: (frame) => {
      console.error('STOMP error:', frame.headers?.message);
    },
  });

  client.activate();
}

export function validarMovimiento(movimiento) {
  if (!client?.connected) return;
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
