import { useNavigate } from 'react-router-dom';
import Button from '../../components/ui/Button';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import AlertHC from '../../components/ui/AlertHC';
import Badge from '../../components/ui/Badge';
import CalendarioSemanal from '../../components/calendar/CalendarioSemanal';
import ConflictoPanel from '../../components/calendar/ConflictoPanel';
import { useCalendario } from '../../hooks/useCalendario';
import { horarioService } from '../../services/horarioService';
import { useState } from 'react';

const SEMESTRE = '2026-1';

export default function CalendarioPage() {
  const navigate = useNavigate();
  const { horario, franjas, loading, recargar } = useCalendario(SEMESTRE);
  const [error, setError] = useState(null);
  const [publicando, setPublicando] = useState(false);

  const publicar = async () => {
    if (!horario?.horarioId) return;
    if (!confirm('¿Publicar el horario? Será visible en la vista pública.')) return;
    setPublicando(true);
    setError(null);
    try {
      await horarioService.publicar(horario.horarioId);
      recargar();
    } catch (err) {
      setError(err.response?.data?.mensaje || 'Error publicando horario');
    } finally {
      setPublicando(false);
    }
  };

  if (loading) return <LoadingSpinner />;

  if (!horario) {
    return (
      <div className="text-center py-16">
        <div className="text-5xl mb-4">📭</div>
        <h2 className="text-xl font-semibold mb-2">Sin horario generado</h2>
        <p className="text-text-secondary mb-6">Todavía no hay horario para {SEMESTRE}.</p>
        <Button variant="primary" onClick={() => navigate('/admin/horario/generar')}>
          Generar Horario
        </Button>
      </div>
    );
  }

  const conflictos = (horario.asignaciones ?? [])
    .filter(a => a.hcViolado)
    .map(a => ({
      seccion: a.seccionGrupo,
      nombreCurso: a.nombreCurso,
      nombreDocente: a.nombreDocente,
      hcViolado: a.hcViolado,
      accionesCorrectivas: [],
    }));
  const esBorrador = horario.estado === 'BORRADOR' || horario.estado === 'CONFLICTO';

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <div className="flex items-center gap-3">
          <h1 className="text-3xl font-bold">Horario {SEMESTRE}</h1>
          <Badge variant={horario.estado}>{horario.estado}</Badge>
        </div>
        <div className="flex gap-3">
          <Button variant="secondary" onClick={() => navigate('/admin/horario/generar')}>
            Regenerar
          </Button>
          {esBorrador && conflictos.length === 0 && (
            <Button variant="success" loading={publicando} onClick={publicar}>
              Publicar
            </Button>
          )}
        </div>
      </div>

      {error && <AlertHC variant="error" className="mb-4">{error}</AlertHC>}

      <CalendarioSemanal
        horario={horario}
        franjas={franjas}
        readOnly={false}
        onUpdate={recargar}
      />

      <ConflictoPanel conflictos={conflictos} />
    </div>
  );
}
