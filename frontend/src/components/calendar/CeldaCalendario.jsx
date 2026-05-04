import { useDroppable } from '@dnd-kit/core';

export default function CeldaCalendario({ id, franja, children, readOnly, validacion }) {
  const { setNodeRef, isOver } = useDroppable({ id, disabled: readOnly || !franja?.esValida });

  if (!franja || !franja.esValida) {
    return (
      <td
        className="h-28 align-middle text-center text-text-muted text-xs border border-border-color/20"
        style={{ background: 'repeating-linear-gradient(45deg,#1f2937 0 8px,#111827 8px 16px)' }}
      >
        —
      </td>
    );
  }

  let bg = 'bg-bg-primary';
  if (isOver && validacion?.valido === true)  bg = 'bg-emerald-900/60';
  if (isOver && validacion?.valido === false) bg = 'bg-red-900/60';

  return (
    <td
      ref={setNodeRef}
      className={`relative h-28 align-top border border-border-color/20 transition-colors ${bg}`}
    >
      {children}
      {isOver && validacion?.valido === false && (
        <div className="absolute bottom-1 left-1 right-1 bg-red-900 text-red-100 text-xs px-1.5 py-1 rounded z-10 truncate">
          {validacion.mensajeError}
        </div>
      )}
    </td>
  );
}
