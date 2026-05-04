import { useDraggable } from '@dnd-kit/core';
import Badge from '../ui/Badge';

export default function BloqueAsignacion({ asignacion, isDragging, disabled }) {
  const { attributes, listeners, setNodeRef, transform } = useDraggable({
    id: asignacion.idAsignacion,
    disabled,
  });

  const conflicto = asignacion.hcViolado != null;

  let bg = 'bg-blue-700 hover:bg-blue-600';
  if (conflicto) bg = 'bg-red-900 hover:bg-red-800';
  if (isDragging) bg = 'bg-purple-700 opacity-60';

  const borderClass = asignacion.estado === 'MANUAL' ? 'border-2 border-accent-yellow' : '';

  const style = transform
    ? { transform: `translate3d(${transform.x}px, ${transform.y}px, 0)`, zIndex: 100 }
    : {};

  return (
    <div
      ref={setNodeRef}
      style={style}
      {...listeners}
      {...attributes}
      className={`p-2 m-1.5 rounded-lg ${bg} ${borderClass} text-white text-xs cursor-grab active:cursor-grabbing select-none transition shadow-sm`}
    >
      <div className="font-semibold leading-snug line-clamp-2">{asignacion.nombreCurso}</div>
      <div className="opacity-80 truncate mt-1">Docente: {asignacion.nombreDocente}</div>
      {asignacion.codigoAula && (
        <div className="opacity-80">Aula: {asignacion.codigoAula}</div>
      )}
      {conflicto && (
        <div className="mt-1">
          <Badge variant={asignacion.hcViolado}>{asignacion.hcViolado}</Badge>
        </div>
      )}
    </div>
  );
}
