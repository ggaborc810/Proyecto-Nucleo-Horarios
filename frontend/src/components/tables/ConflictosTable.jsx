import { useState } from 'react';
import Badge from '../ui/Badge';

export default function ConflictosTable({ conflictos }) {
  const [expandido, setExpandido] = useState(true);
  if (!conflictos || conflictos.length === 0) return null;

  return (
    <div className="mt-4 bg-bg-secondary rounded-xl border border-red-900 overflow-hidden">
      <button
        onClick={() => setExpandido(!expandido)}
        className="w-full flex justify-between items-center px-4 py-3 bg-red-900/20 hover:bg-red-900/30 transition"
      >
        <span className="text-red-300 font-semibold text-sm">
          ⚠ Conflictos sin resolver ({conflictos.length})
        </span>
        <span className="text-red-300">{expandido ? '▾' : '▸'}</span>
      </button>
      {expandido && (
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="bg-bg-tertiary text-xs text-text-secondary">
                <th className="px-4 py-2 text-left">Sección</th>
                <th className="px-4 py-2 text-left">Curso</th>
                <th className="px-4 py-2 text-left">Docente</th>
                <th className="px-4 py-2 text-left">HC violado</th>
                <th className="px-4 py-2 text-left">Acción sugerida</th>
              </tr>
            </thead>
            <tbody>
              {conflictos.map((c, i) => (
                <tr key={i} className="border-t border-border-color text-sm text-text-primary">
                  <td className="px-4 py-2">{c.seccion}</td>
                  <td className="px-4 py-2">{c.nombreCurso}</td>
                  <td className="px-4 py-2">{c.nombreDocente}</td>
                  <td className="px-4 py-2"><Badge variant={c.hcViolado}>{c.hcViolado}</Badge></td>
                  <td className="px-4 py-2 text-text-secondary text-xs">{c.accionesCorrectivas?.[0]}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
