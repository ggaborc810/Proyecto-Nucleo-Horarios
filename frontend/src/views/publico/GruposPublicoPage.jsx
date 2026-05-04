import { useState, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import AlertHC from '../../components/ui/AlertHC';
import Badge from '../../components/ui/Badge';
import { grupoService } from '../../services/grupoService';
import { matchesSearch, normalizeSearch } from '../../utils/search';

export default function GruposPublicoPage() {
  const navigate = useNavigate();
  const [grupos, setGrupos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [busqueda, setBusqueda] = useState('');

  useEffect(() => {
    grupoService.listarPublicos()
      .then(setGrupos)
      .catch(() => setError('No se pudo cargar el listado de grupos'))
      .finally(() => setLoading(false));
  }, []);

  const gruposFiltrados = useMemo(() => {
    const q = normalizeSearch(busqueda);
    if (!q) return grupos;
    return grupos.filter(g =>
      matchesSearch(g.nombreCurso, q) ||
      matchesSearch(g.seccion, q) ||
      matchesSearch(g.nombreDocente, q)
    );
  }, [grupos, busqueda]);

  const cuposDisponibles = (g) => {
    const max = 40;
    return Math.max(0, max - g.numInscritos);
  };

  return (
    <div>
      <div className="mb-6">
        <h2 className="text-2xl font-bold text-text-primary">Grupos Disponibles</h2>
        <p className="text-text-secondary text-sm mt-1">Consulta los grupos activos y sus cupos</p>
      </div>

      {error && <AlertHC variant="error">{error}</AlertHC>}

      <div className="mb-4">
        <input
          type="text"
          placeholder="Buscar por materia, sección o docente..."
          value={busqueda}
          onChange={e => setBusqueda(e.target.value)}
          className="w-full max-w-md px-4 py-2 bg-bg-secondary text-text-primary rounded-lg border border-border-color text-sm focus:outline-none focus:border-accent-blue"
        />
      </div>

      {loading ? (
        <LoadingSpinner />
      ) : gruposFiltrados.length === 0 ? (
        <p className="text-text-secondary text-sm py-8 text-center">
          {busqueda ? 'No se encontraron grupos con ese criterio.' : 'No hay grupos activos.'}
        </p>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
          {gruposFiltrados.map(g => {
            const cupos = cuposDisponibles(g);
            return (
              <button
                key={g.grupoId}
                onClick={() => navigate(`/grupos/${g.grupoId}`)}
                className="text-left bg-bg-secondary border border-border-color rounded-xl p-4 hover:border-accent-blue/50 transition"
              >
                <div className="flex justify-between items-start mb-2">
                  <span className="font-semibold text-text-primary text-sm leading-tight pr-2">
                    {g.nombreCurso}
                  </span>
                  <Badge variant={cupos > 5 ? 'ACTIVO' : cupos > 0 ? 'CERRADO' : 'CONFLICTO'}>
                    {cupos > 0 ? `${cupos} cupos` : 'Lleno'}
                  </Badge>
                </div>
                <div className="text-xs text-text-secondary space-y-1">
                  <div>Sección: <span className="text-text-primary">{g.seccion}</span></div>
                  <div>Docente: <span className="text-text-primary">{g.nombreDocente}</span></div>
                  <div>Inscritos: <span className="text-text-primary">{g.numInscritos}</span></div>
                </div>
              </button>
            );
          })}
        </div>
      )}
    </div>
  );
}
