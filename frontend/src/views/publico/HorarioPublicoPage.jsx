import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import AlertHC from '../../components/ui/AlertHC';
import CalendarioSemanal from '../../components/calendar/CalendarioSemanal';
import { horarioService } from '../../services/horarioService';
import { matchesSearch } from '../../utils/search';

export default function HorarioPublicoPage() {
  const { semestre } = useParams();
  const [horario, setHorario] = useState(null);
  const [franjas, setFranjas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [filtroDocente, setFiltroDocente] = useState('');
  const [filtroMateria, setFiltroMateria] = useState('');

  useEffect(() => {
    setLoading(true);
    setError(null);
    horarioService.obtenerPublico(semestre)
      .then(data => {
        setHorario(data);
        setFranjas(data?.franjas ?? []);
      })
      .catch(() => setError('No hay horario publicado para el semestre ' + semestre))
      .finally(() => setLoading(false));
  }, [semestre]);

  const horarioFiltrado = () => {
    if (!horario) return null;
    let asignaciones = horario.asignaciones ?? [];
    if (filtroDocente) asignaciones = asignaciones.filter(a => matchesSearch(a.nombreDocente, filtroDocente));
    if (filtroMateria) asignaciones = asignaciones.filter(a => matchesSearch(a.nombreCurso, filtroMateria));
    return { ...horario, asignaciones };
  };

  if (loading) return <LoadingSpinner />;

  if (error) return (
    <div className="text-center py-16">
      <div className="text-5xl mb-4">📭</div>
      <AlertHC variant="info">{error}</AlertHC>
      <p className="text-text-secondary text-sm mt-3">
        El horario se publica una vez que el equipo académico finaliza la programación del semestre.
      </p>
    </div>
  );

  const sinAsignaciones = (horario?.asignaciones ?? []).length === 0;

  if (sinAsignaciones) return (
    <div className="text-center py-16">
      <div className="text-5xl mb-4">🗓️</div>
      <h2 className="text-xl font-semibold text-text-primary mb-2">Horario en preparación</h2>
      <p className="text-text-secondary text-sm">
        El horario del semestre <strong>{semestre}</strong> aún está siendo programado.<br />
        Vuelve pronto para ver las sesiones asignadas.
      </p>
    </div>
  );

  return (
    <div>
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-6">
        <div>
          <h2 className="text-2xl font-bold text-text-primary">Horario — {semestre}</h2>
          <p className="text-text-secondary text-sm mt-1">Vista pública. Sin credenciales requeridas.</p>
        </div>
        <div className="flex flex-col sm:flex-row gap-3">
          <input
            placeholder="Filtrar por docente..."
            value={filtroDocente}
            onChange={e => setFiltroDocente(e.target.value)}
            className="px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm w-full sm:w-48"
          />
          <input
            placeholder="Filtrar por materia..."
            value={filtroMateria}
            onChange={e => setFiltroMateria(e.target.value)}
            className="px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm w-full sm:w-56"
          />
        </div>
      </div>

      <CalendarioSemanal
        horario={horarioFiltrado()}
        franjas={franjas}
        readOnly={true}
        onUpdate={() => {}}
      />
    </div>
  );
}
