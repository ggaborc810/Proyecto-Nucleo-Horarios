import { useState, useEffect } from 'react';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import AlertHC from '../../components/ui/AlertHC';
import CalendarioSemanal from '../../components/calendar/CalendarioSemanal';
import { horarioService } from '../../services/horarioService';
import { matchesSearch } from '../../utils/search';

const SEMESTRE = '2026-1';

export default function HorarioGeneralPage() {
  const [horario, setHorario] = useState(null);
  const [franjas, setFranjas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [filtroDocente, setFiltroDocente] = useState('');
  const [filtroAula, setFiltroAula] = useState('');

  useEffect(() => {
    horarioService.obtenerPublico(SEMESTRE)
      .then(data => {
        setHorario(data);
        setFranjas(data?.franjas ?? []);
      })
      .catch(() => setError('No hay horario publicado para ' + SEMESTRE))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <LoadingSpinner />;

  const horarioFiltrado = () => {
    if (!horario) return null;
    let asignaciones = horario.asignaciones ?? [];
    if (filtroDocente) asignaciones = asignaciones.filter(a => matchesSearch(a.nombreDocente, filtroDocente));
    if (filtroAula) asignaciones = asignaciones.filter(a => matchesSearch(a.codigoAula, filtroAula));
    return { ...horario, asignaciones };
  };

  return (
    <div>
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
        <div>
          <h1 className="text-2xl font-bold text-text-primary">Horario General</h1>
          <p className="text-text-secondary text-sm mt-1">Vista completa del semestre {SEMESTRE}</p>
        </div>
        <div className="flex gap-3">
          <input
            placeholder="Filtrar por docente..."
            value={filtroDocente}
            onChange={e => setFiltroDocente(e.target.value)}
            className="px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm w-44"
          />
          <input
            placeholder="Filtrar por aula..."
            value={filtroAula}
            onChange={e => setFiltroAula(e.target.value)}
            className="px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm w-36"
          />
        </div>
      </div>

      {error ? (
        <AlertHC variant="info">{error}</AlertHC>
      ) : (
        <CalendarioSemanal
          horario={horarioFiltrado()}
          franjas={franjas}
          readOnly={true}
          onUpdate={() => {}}
        />
      )}
    </div>
  );
}
