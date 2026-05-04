import { useState, useEffect } from 'react';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import AlertHC from '../../components/ui/AlertHC';
import Badge from '../../components/ui/Badge';
import { grupoService } from '../../services/grupoService';

export default function MisGruposPage() {
  const [grupos, setGrupos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const docenteId = localStorage.getItem('docenteId');
    if (!docenteId) {
      setError('No se encontró tu identificador de docente.');
      setLoading(false);
      return;
    }
    grupoService.listar(undefined)
      .then(data => setGrupos(data.filter(g => g.docenteId === parseInt(docenteId))))
      .catch(() => setError('Error cargando grupos'))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <LoadingSpinner />;

  return (
    <div>
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-text-primary">Mis Grupos</h1>
        <p className="text-text-secondary text-sm mt-1">Grupos que tienes asignados este semestre</p>
      </div>

      {error && <AlertHC variant="error">{error}</AlertHC>}

      {!error && grupos.length === 0 && (
        <AlertHC variant="info">No tienes grupos asignados actualmente.</AlertHC>
      )}

      {grupos.length > 0 && (
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
          {grupos.map(g => (
            <div key={g.grupoId} className="bg-bg-secondary border border-border-color rounded-xl p-4">
              <div className="flex justify-between items-start mb-2">
                <span className="font-semibold text-text-primary text-sm">{g.nombreCurso}</span>
                <Badge variant={g.estado}>{g.estado}</Badge>
              </div>
              <div className="text-xs text-text-secondary space-y-1">
                <div>Sección: <span className="text-text-primary">{g.seccion}</span></div>
                <div>Inscritos: <span className="text-text-primary font-medium">{g.numInscritos}</span></div>
                {g.fechaCierre && (
                  <div>Cierre: <span className="text-text-primary">{g.fechaCierre}</span></div>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
