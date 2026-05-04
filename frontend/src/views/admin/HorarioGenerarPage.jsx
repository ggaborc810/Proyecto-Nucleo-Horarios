import { useState, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import Button from '../../components/ui/Button';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import AlertHC from '../../components/ui/AlertHC';
import ConflictosTable from '../../components/tables/ConflictosTable';
import { horarioService } from '../../services/horarioService';
import api from '../../services/api';
import { matchesSearch, normalizeSearch } from '../../utils/search';

const SEMESTRE = '2026-1';
const MIN_MATERIAS = 1;
const MAX_MATERIAS = 81;

export default function HorarioGenerarPage() {
  const navigate = useNavigate();

  const [cursos, setCursos] = useState([]);
  const [cargandoCursos, setCargandoCursos] = useState(true);
  const [seleccionados, setSeleccionados] = useState([]);
  const [busqueda, setBusqueda] = useState('');

  const [generando, setGenerando] = useState(false);
  const [resultado, setResultado] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    api.get('/cursos')
      .then(data => setCursos(data))
      .catch(() => setError('No se pudieron cargar las materias'))
      .finally(() => setCargandoCursos(false));
  }, []);

  const cursosFiltrados = useMemo(() => {
    const q = normalizeSearch(busqueda);
    return cursos.filter(c =>
      matchesSearch(c.nombreCurso, q) ||
      matchesSearch(c.codigoCurso, q)
    );
  }, [cursos, busqueda]);

  const toggleCurso = (id) => {
    setSeleccionados(prev => {
      if (prev.includes(id)) return prev.filter(x => x !== id);
      if (prev.length >= MAX_MATERIAS) return prev;
      return [...prev, id];
    });
  };

  const seleccionarTodos = () => {
    const visibles = cursosFiltrados.map(c => c.cursoId);
    const nuevos = [...new Set([...seleccionados, ...visibles])].slice(0, MAX_MATERIAS);
    setSeleccionados(nuevos);
  };

  const limpiarSeleccion = () => setSeleccionados([]);

  const generar = async () => {
    setGenerando(true);
    setError(null);
    setResultado(null);
    try {
      const data = await horarioService.generar(SEMESTRE, seleccionados);
      setResultado(data);
    } catch (err) {
      setError(err.response?.data?.mensaje || 'Error generando horario. Verifica que los grupos tengan docentes y franjas configuradas.');
    } finally {
      setGenerando(false);
    }
  };

  const publicar = async () => {
    if (!resultado?.horarioId) return;
    try {
      await horarioService.publicar(resultado.horarioId);
      navigate('/admin/horario');
    } catch (err) {
      setError(err.response?.data?.mensaje || 'Error publicando horario');
    }
  };

  const puedeGenerar = seleccionados.length >= MIN_MATERIAS && seleccionados.length <= MAX_MATERIAS;

  if (generando) {
    return (
      <div className="max-w-4xl">
        <h1 className="text-3xl font-bold mb-2">Generar Horario</h1>
        <div className="bg-bg-secondary rounded-xl border border-border-color p-8 text-center mt-8">
          <LoadingSpinner />
          <p className="text-text-secondary mt-4">Ejecutando algoritmo Greedy... esto puede tardar hasta 60 segundos.</p>
        </div>
      </div>
    );
  }

  if (resultado) {
    return (
      <div className="max-w-4xl">
        <h1 className="text-3xl font-bold mb-2">Resultado — {SEMESTRE}</h1>
        {error && <AlertHC variant="error" className="mb-4">{error}</AlertHC>}
        <div className="space-y-6">
          <div className="grid grid-cols-3 gap-4">
            <div className="bg-bg-secondary rounded-xl border border-border-color p-4 text-center">
              <div className="text-3xl font-bold text-accent-green">{resultado.totalAsignadas ?? 0}</div>
              <div className="text-text-secondary text-sm mt-1">Asignaciones exitosas</div>
            </div>
            <div className="bg-bg-secondary rounded-xl border border-border-color p-4 text-center">
              <div className="text-3xl font-bold text-accent-red">{resultado.totalConflictos ?? 0}</div>
              <div className="text-text-secondary text-sm mt-1">Conflictos HC</div>
            </div>
            <div className="bg-bg-secondary rounded-xl border border-border-color p-4 text-center">
              <div className="text-3xl font-bold text-accent-blue">{resultado.tiempoEjecucionMs ?? 0} ms</div>
              <div className="text-text-secondary text-sm mt-1">Tiempo de ejecución</div>
            </div>
          </div>

          <div className="flex gap-3">
            <Button variant="secondary" onClick={() => setResultado(null)}>Volver a selección</Button>
            <Button variant="secondary" onClick={generar}>Regenerar</Button>
            <Button variant="primary" onClick={() => navigate('/admin/horario')}>Ver en Calendario</Button>
            {(!resultado.conflictos || resultado.conflictos.length === 0) && (
              <Button variant="success" onClick={publicar}>Publicar Horario</Button>
            )}
          </div>

          {resultado.conflictos && resultado.conflictos.length > 0 && (
            <ConflictosTable conflictos={resultado.conflictos} />
          )}
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-4xl">
      <h1 className="text-3xl font-bold mb-2">Generar Horario</h1>
      <p className="text-text-secondary mb-6">
        Selecciona las materias a incluir en el horario del semestre <strong>{SEMESTRE}</strong>.
        Puedes generar el horario completo o solo para un subconjunto de cursos.
      </p>

      {error && <AlertHC variant="error" className="mb-4">{error}</AlertHC>}

      {/* Barra de selección */}
      <div className="bg-bg-secondary rounded-xl border border-border-color p-4 mb-4">
        <div className="flex flex-col sm:flex-row gap-3 items-start sm:items-center justify-between">
          <input
            type="text"
            placeholder="Buscar por nombre o código..."
            value={busqueda}
            onChange={e => setBusqueda(e.target.value)}
            className="flex-1 bg-bg-primary border border-border-color rounded-lg px-3 py-2 text-sm text-text-primary placeholder-text-secondary focus:outline-none focus:border-accent-blue"
          />
          <div className="flex gap-2 shrink-0">
            <button
              onClick={seleccionarTodos}
              disabled={seleccionados.length >= MAX_MATERIAS}
              className="text-xs px-3 py-1.5 rounded-lg border border-border-color text-text-secondary hover:text-text-primary hover:border-accent-blue transition disabled:opacity-40 disabled:cursor-not-allowed"
            >
              Seleccionar visibles
            </button>
            <button
              onClick={limpiarSeleccion}
              disabled={seleccionados.length === 0}
              className="text-xs px-3 py-1.5 rounded-lg border border-border-color text-text-secondary hover:text-accent-red hover:border-accent-red transition disabled:opacity-40 disabled:cursor-not-allowed"
            >
              Limpiar
            </button>
          </div>
        </div>

        {/* Contador */}
        <div className="mt-3">
          <span className={`text-sm font-medium ${
            seleccionados.length < MIN_MATERIAS ? 'text-accent-red' : 'text-accent-blue'
          }`}>
            {seleccionados.length} materia{seleccionados.length !== 1 ? 's' : ''} seleccionada{seleccionados.length !== 1 ? 's' : ''}
            {seleccionados.length < MIN_MATERIAS && ' (selecciona al menos una)'}
          </span>
        </div>
      </div>

      {/* Lista de cursos */}
      {cargandoCursos ? (
        <div className="flex justify-center py-12"><LoadingSpinner /></div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 max-h-[420px] overflow-y-auto pr-1">
          {cursosFiltrados.length === 0 ? (
            <p className="col-span-2 text-center text-text-secondary py-8">
              No se encontraron materias con ese criterio.
            </p>
          ) : (
            cursosFiltrados.map(curso => {
              const activo = seleccionados.includes(curso.cursoId);
              const bloqueado = !activo && seleccionados.length >= MAX_MATERIAS;
              return (
                <button
                  key={curso.cursoId}
                  onClick={() => toggleCurso(curso.cursoId)}
                  disabled={bloqueado}
                  className={`text-left p-3 rounded-xl border transition-all ${
                    activo
                      ? 'border-accent-blue bg-accent-blue/10 text-text-primary'
                      : bloqueado
                        ? 'border-border-color bg-bg-secondary opacity-40 cursor-not-allowed'
                        : 'border-border-color bg-bg-secondary hover:border-accent-blue/50 text-text-primary'
                  }`}
                >
                  <div className="flex items-start gap-3">
                    <div className={`mt-0.5 w-4 h-4 rounded border shrink-0 flex items-center justify-center transition-colors ${
                      activo ? 'bg-accent-blue border-accent-blue' : 'border-border-color'
                    }`}>
                      {activo && (
                        <svg className="w-2.5 h-2.5 text-white" fill="none" viewBox="0 0 10 8">
                          <path d="M1 4l3 3 5-6" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
                        </svg>
                      )}
                    </div>
                    <div className="min-w-0">
                      <div className="text-sm font-medium truncate">{curso.nombreCurso}</div>
                      <div className="text-xs text-text-secondary mt-0.5">
                        {curso.codigoCurso} · Sem {curso.semestreNivel} · {curso.frecuenciaSemanal}x/sem
                      </div>
                    </div>
                  </div>
                </button>
              );
            })
          )}
        </div>
      )}

      {/* Acción */}
      <div className="mt-6 flex justify-end">
        <Button
          variant="primary"
          size="lg"
          onClick={generar}
          disabled={!puedeGenerar}
        >
          {!puedeGenerar
            ? 'Selecciona al menos una materia'
            : `Generar Horario con ${seleccionados.length} materia${seleccionados.length !== 1 ? 's' : ''}`}
        </Button>
      </div>
    </div>
  );
}
