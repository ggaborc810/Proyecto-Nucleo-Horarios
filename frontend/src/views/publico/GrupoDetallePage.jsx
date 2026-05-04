import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import AlertHC from '../../components/ui/AlertHC';
import Badge from '../../components/ui/Badge';
import Button from '../../components/ui/Button';
import { grupoService } from '../../services/grupoService';

export default function GrupoDetallePage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [grupo, setGrupo] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    grupoService.obtenerPublico(id)
      .then(setGrupo)
      .catch(() => setError('No se encontró el grupo.'))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <LoadingSpinner />;

  return (
    <div className="max-w-lg mx-auto">
      <div className="mb-4">
        <Button variant="ghost" size="sm" onClick={() => navigate(-1)}>← Volver</Button>
      </div>

      {error ? (
        <AlertHC variant="error">{error}</AlertHC>
      ) : grupo && (
        <div className="bg-bg-secondary border border-border-color rounded-xl p-6 space-y-4">
          <div className="flex justify-between items-start">
            <h2 className="text-xl font-bold text-text-primary">{grupo.nombreCurso}</h2>
            <Badge variant={grupo.estado}>{grupo.estado}</Badge>
          </div>

          <div className="grid grid-cols-2 gap-3 text-sm">
            <div>
              <div className="text-text-secondary text-xs mb-0.5">Sección</div>
              <div className="text-text-primary font-medium">{grupo.seccion}</div>
            </div>
            <div>
              <div className="text-text-secondary text-xs mb-0.5">Inscritos</div>
              <div className="text-text-primary font-medium">{grupo.numInscritos}</div>
            </div>
            <div className="col-span-2">
              <div className="text-text-secondary text-xs mb-0.5">Docente</div>
              <div className="text-text-primary font-medium">{grupo.nombreDocente}</div>
            </div>
          </div>

          {grupo.fechaCierre && (
            <div className="text-xs text-text-secondary border-t border-border-color pt-3">
              Cerrado el {grupo.fechaCierre}
              {grupo.causaCierre && ` — ${grupo.causaCierre}`}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
