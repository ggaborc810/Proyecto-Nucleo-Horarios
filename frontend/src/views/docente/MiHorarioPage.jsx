import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import CalendarioSemanal from '../../components/calendar/CalendarioSemanal';
import Button from '../../components/ui/Button';
import { horarioService } from '../../services/horarioService';

const SEMESTRE = '2026-1';

function SinHorario({ motivo }) {
  const navigate = useNavigate();
  return (
    <div className="flex flex-col items-center justify-center py-16 text-center">
      <div className="w-20 h-20 rounded-full bg-bg-secondary border border-border-color flex items-center justify-center mb-5">
        <svg className="w-9 h-9 text-text-secondary" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5}
            d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
        </svg>
      </div>
      <h2 className="text-xl font-semibold text-text-primary mb-2">
        Aún no tienes horario asignado
      </h2>
      <p className="text-text-secondary text-sm max-w-sm mb-1">
        {motivo === 'no-publicado'
          ? 'El horario del semestre aún no ha sido publicado por la coordinación.'
          : 'No tienes sesiones asignadas en el horario actual.'}
      </p>
      <p className="text-text-secondary text-xs max-w-sm mb-6">
        Una vez que el coordinador genere y publique el horario, tus sesiones aparecerán aquí.
      </p>
      <div className="flex gap-3">
        <Button variant="secondary" onClick={() => navigate('/docente/disponibilidad')}>
          Revisar mi disponibilidad
        </Button>
        <Button variant="ghost" onClick={() => navigate('/docente/mis-grupos')}>
          Ver mis grupos
        </Button>
      </div>
    </div>
  );
}

export default function MiHorarioPage() {
  const [horario, setHorario] = useState(null);
  const [franjas, setFranjas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [motivo, setMotivo] = useState(null);

  useEffect(() => {
    const docenteId = localStorage.getItem('docenteId');
    if (!docenteId) {
      setMotivo('sin-id');
      setLoading(false);
      return;
    }

    horarioService.obtenerPublicoPorDocente(SEMESTRE, docenteId)
      .then(data => {
        if (!data || (data.asignaciones ?? []).length === 0) {
          setMotivo('sin-sesiones');
        } else {
          setHorario(data);
          setFranjas(data?.franjas ?? []);
        }
      })
      .catch(() => setMotivo('no-publicado'))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <LoadingSpinner />;

  return (
    <div>
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-text-primary">Mi Horario</h1>
        <p className="text-text-secondary text-sm mt-1">Semestre {SEMESTRE} · tus sesiones asignadas</p>
      </div>

      {motivo ? (
        <SinHorario motivo={motivo} />
      ) : (
        <CalendarioSemanal
          horario={horario}
          franjas={franjas}
          readOnly={true}
          onUpdate={() => {}}
        />
      )}
    </div>
  );
}
