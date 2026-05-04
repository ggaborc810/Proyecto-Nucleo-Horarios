# Vista Calendario — Drag-and-Drop

Esta es la vista más crítica del sistema. Implementar con prioridad alta.

## Layout

```
┌─────────────────────────────────────────────────────────────────────┐
│  SEMESTRE: 2026-1  [● BORRADOR]    [🔄 Regenerar]  [📢 Publicar]    │
├─────────────┬───────────────────────────────────────────────────────┤
│             │  LUN     MAR     MIÉ     JUE     VIE     SÁB         │
│  07:00–09:00│ [   ]   [   ]   [   ]   [   ]   [   ]   [   ]       │
│  09:00–11:00│ [   ]   [   ]   [   ]   [   ]   [   ]   [   ]       │
│  11:00–13:00│ [▓▓▓]   [▓▓▓]   [▓▓▓]   [▓▓▓]   [▓▓▓]   [▓▓▓]      │  ← Excluido
│  13:00–15:00│ [   ]   [   ]   [   ]   [   ]   [   ]    —          │
│  ...        │                                                       │
│  20:00–22:00│ [   ]   [   ]   [   ]   [   ]   [   ]    —          │
├─────────────┴───────────────────────────────────────────────────────┤
│ ⚠ Conflictos (3) — panel inferior expandible                       │
└─────────────────────────────────────────────────────────────────────┘
```

## Bloque de Asignación

```
┌─────────────────────┐
│ 🏫 IS-301 G1        │  ← Curso + grupo
│ 👤 García López     │  ← Docente
│ 📍 Aula 301         │  ← Aula
└─────────────────────┘
```

Estados visuales:
- **Asignado normal**: fondo `--cal-cell-assigned` (azul oscuro)
- **Conflicto**: fondo `--cal-cell-conflict` (rojo oscuro) + ícono ⚠️
- **Movido manualmente**: borde `--accent-yellow` (amarillo)
- **Siendo arrastrado**: fondo `--cal-cell-dragging` (púrpura) + opacity 0.7

## Comportamiento Drag-and-Drop

1. Usuario hace `mousedown` sobre un bloque → bloque entra en modo dragging
2. Mientras arrastra, el bloque se mueve con el cursor (snapshot semitransparente)
3. Al pasar sobre una celda destino → el frontend envía `MovimientoDTO` por WebSocket
4. El backend evalúa los 10 HC y devuelve `ValidacionMovimientoDTO`
5. La celda destino se ilumina:
   - **Verde** (`--cal-cell-hover-valid`) si `valido = true`
   - **Rojo** (`--cal-cell-hover-invalid`) + tooltip con `mensajeError` si `valido = false`
6. Al soltar:
   - Si válido → llamada REST `PUT /api/asignaciones/:id/mover` para confirmar
   - Si inválido → animación de retorno a posición original

## Implementación con @dnd-kit

### CalendarioSemanal.jsx

```jsx
import { DndContext, closestCenter } from '@dnd-kit/core';
import { useState, useEffect } from 'react';
import CeldaCalendario from './CeldaCalendario';
import BloqueAsignacion from './BloqueAsignacion';
import { useWebSocket } from '../../hooks/useWebSocket';
import { asignacionService } from '../../services/asignacionService';

const DIAS = ['LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO'];

export default function CalendarioSemanal({ horario, franjas, readOnly = false, onUpdate }) {
  const [activeId, setActiveId] = useState(null);
  const [validacion, setValidacion] = useState({});  // { celdaId: { valido, mensaje } }
  const { validarMovimiento } = useWebSocket(setValidacion);

  // Agrupar franjas únicas por hora_inicio
  const franjasUnicas = [...new Set(franjas.map(f => f.horaInicio))].sort();

  // Mapa para acceso rápido: dia + hora_inicio → asignación
  const mapaAsignaciones = {};
  horario.asignaciones.forEach(a => {
    mapaAsignaciones[`${a.diaSemana}_${a.horaInicio}`] = a;
  });

  const onDragStart = ({ active }) => {
    setActiveId(active.id);
  };

  const onDragOver = ({ over }) => {
    if (!over || readOnly) return;
    const [diaDest, horaDest] = over.id.split('_');
    const asignacion = mapaAsignaciones[`__activa__${activeId}`];
    if (!asignacion) return;

    const franjaDest = franjas.find(f => f.diaSemana === diaDest && f.horaInicio === horaDest);
    if (!franjaDest) return;

    validarMovimiento({
      asignacionId: asignacion.idAsignacion,
      nuevaFranjaId: franjaDest.franjaId,
      nuevaAulaId: asignacion.aulaId,  // mantener el aula por ahora
    });
  };

  const onDragEnd = async ({ active, over }) => {
    setActiveId(null);
    setValidacion({});

    if (!over || readOnly) return;
    const [diaDest, horaDest] = over.id.split('_');
    const asignacion = horario.asignaciones.find(a => a.idAsignacion === active.id);
    const franjaDest = franjas.find(f => f.diaSemana === diaDest && f.horaInicio === horaDest);

    if (!franjaDest) return;

    try {
      await asignacionService.mover(asignacion.idAsignacion, franjaDest.franjaId, asignacion.aulaId);
      onUpdate();  // recargar horario
    } catch (err) {
      // Error 409 → mostrar toast con HC violado
      console.error('Movimiento rechazado:', err.response?.data);
    }
  };

  return (
    <DndContext
      collisionDetection={closestCenter}
      onDragStart={onDragStart}
      onDragOver={onDragOver}
      onDragEnd={onDragEnd}
    >
      <div className="bg-bg-secondary rounded-xl border border-border-color overflow-x-auto">
        <table className="w-full">
          <thead>
            <tr className="bg-bg-tertiary">
              <th className="px-4 py-3 text-left text-sm text-text-secondary w-32">Hora</th>
              {DIAS.map(d => (
                <th key={d} className="px-4 py-3 text-center text-sm text-text-secondary">{d}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {franjasUnicas.map(hora => {
              const franjaRef = franjas.find(f => f.horaInicio === hora);
              const horaFin = franjaRef?.horaValida;
              return (
                <tr key={hora} className="border-t border-border-color">
                  <td className="px-4 py-3 text-text-secondary text-sm">
                    {hora.slice(0, 5)}–{horaFin?.slice(0, 5)}
                  </td>
                  {DIAS.map(dia => {
                    const franja = franjas.find(f => f.diaSemana === dia && f.horaInicio === hora);
                    const asignacion = mapaAsignaciones[`${dia}_${hora}`];
                    const celdaId = `${dia}_${hora}`;
                    return (
                      <CeldaCalendario
                        key={celdaId}
                        id={celdaId}
                        franja={franja}
                        readOnly={readOnly}
                        validacion={validacion[celdaId]}
                      >
                        {asignacion && (
                          <BloqueAsignacion
                            asignacion={asignacion}
                            isDragging={activeId === asignacion.idAsignacion}
                            disabled={readOnly}
                          />
                        )}
                      </CeldaCalendario>
                    );
                  })}
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </DndContext>
  );
}
```

### CeldaCalendario.jsx

```jsx
import { useDroppable } from '@dnd-kit/core';

export default function CeldaCalendario({ id, franja, children, readOnly, validacion }) {
  const { setNodeRef, isOver } = useDroppable({ id, disabled: readOnly });

  if (!franja || !franja.esValida) {
    return (
      <td className="bg-stripe text-center text-text-muted text-xs"
          style={{ background: 'repeating-linear-gradient(45deg, #1f2937 0 8px, #111827 8px 16px)' }}>
        —
      </td>
    );
  }

  let bgClass = 'bg-bg-primary';
  if (isOver && validacion?.valido === true)  bgClass = 'bg-emerald-900';
  if (isOver && validacion?.valido === false) bgClass = 'bg-red-900';

  return (
    <td
      ref={setNodeRef}
      className={`relative h-20 align-middle border border-border-color/30 transition ${bgClass}`}
    >
      {children}
      {isOver && validacion?.valido === false && (
        <div className="absolute top-1 right-1 bg-red-900 text-red-100 text-xs px-2 py-1 rounded">
          {validacion.mensajeError}
        </div>
      )}
    </td>
  );
}
```

### BloqueAsignacion.jsx

```jsx
import { useDraggable } from '@dnd-kit/core';
import Badge from '../ui/Badge';

export default function BloqueAsignacion({ asignacion, isDragging, disabled }) {
  const { attributes, listeners, setNodeRef, transform } = useDraggable({
    id: asignacion.idAsignacion,
    disabled,
  });

  const conflicto = asignacion.hcViolado != null;
  const manual = asignacion.estado === 'MANUAL';

  let bg = 'bg-blue-700';
  if (conflicto) bg = 'bg-red-900';
  if (isDragging) bg = 'bg-purple-700 opacity-70';

  const borderClass = manual ? 'border-2 border-accent-yellow' : '';

  const style = transform ? {
    transform: `translate3d(${transform.x}px, ${transform.y}px, 0)`,
    zIndex: 100,
  } : {};

  return (
    <div
      ref={setNodeRef}
      style={style}
      {...listeners}
      {...attributes}
      className={`p-2 m-1 rounded-lg ${bg} ${borderClass} text-white text-xs cursor-grab active:cursor-grabbing`}
    >
      <div className="font-semibold">🏫 {asignacion.nombreCurso}</div>
      <div className="opacity-80">👤 {asignacion.nombreDocente}</div>
      <div className="opacity-80">📍 {asignacion.codigoAula}</div>
      {conflicto && <Badge variant={asignacion.hcViolado}>{asignacion.hcViolado}</Badge>}
    </div>
  );
}
```

### ConflictoPanel.jsx

```jsx
export default function ConflictoPanel({ conflictos }) {
  const [expandido, setExpandido] = useState(true);
  if (conflictos.length === 0) return null;

  return (
    <div className="mt-4 bg-bg-secondary rounded-xl border border-red-900 overflow-hidden">
      <button
        onClick={() => setExpandido(!expandido)}
        className="w-full flex justify-between items-center px-4 py-3 bg-red-900/30"
      >
        <span className="text-red-200 font-semibold">
          ⚠️ Conflictos ({conflictos.length})
        </span>
        <span className="text-red-200">{expandido ? '▾' : '▸'}</span>
      </button>
      {expandido && (
        <table className="w-full">
          <thead>
            <tr className="bg-bg-tertiary text-sm text-text-secondary">
              <th className="px-4 py-2 text-left">Grupo</th>
              <th className="px-4 py-2 text-left">Curso</th>
              <th className="px-4 py-2 text-left">Docente</th>
              <th className="px-4 py-2 text-left">HC</th>
              <th className="px-4 py-2 text-left">Acción</th>
            </tr>
          </thead>
          <tbody>
            {conflictos.map((c, i) => (
              <tr key={i} className="border-t border-border-color text-text-primary">
                <td className="px-4 py-2">{c.seccion}</td>
                <td className="px-4 py-2">{c.nombreCurso}</td>
                <td className="px-4 py-2">{c.nombreDocente}</td>
                <td className="px-4 py-2"><Badge variant={c.hcViolado}>{c.hcViolado}</Badge></td>
                <td className="px-4 py-2 text-xs text-text-secondary">
                  {c.accionesCorrectivas[0]}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
```

## Hook useCalendario

```js
import { useState, useEffect, useCallback } from 'react';
import { horarioService } from '../services/horarioService';

export function useCalendario(semestre) {
  const [horario, setHorario] = useState(null);
  const [franjas, setFranjas] = useState([]);
  const [loading, setLoading] = useState(true);

  const recargar = useCallback(async () => {
    setLoading(true);
    const [h, fs] = await Promise.all([
      horarioService.obtener(semestre),
      horarioService.franjas(semestre),
    ]);
    setHorario(h);
    setFranjas(fs);
    setLoading(false);
  }, [semestre]);

  useEffect(() => { recargar(); }, [recargar]);

  return { horario, franjas, loading, recargar };
}
```

## Sábado — Caso Especial

Las celdas en sábado después de las 10:00 (último bloque válido 10:00–12:00 si no aplica exclusión) se renderizan con guión "—" y patrón rayado:

```jsx
{esSabado && esHoraTarde && <td className="text-center text-text-muted">—</td>}
```

## Vista Pública del Calendario

Reutiliza `CalendarioSemanal` con `readOnly={true}`. No instancia `DndContext` con handlers; las celdas no son drop targets.
