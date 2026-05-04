import { useState, useEffect, useCallback } from 'react';
import Button from '../../components/ui/Button';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import DataTable from '../../components/tables/DataTable';
import Modal from '../../components/ui/Modal';
import AlertHC from '../../components/ui/AlertHC';
import DisponibilidadForm from '../../components/forms/DisponibilidadForm';
import { docenteService } from '../../services/docenteService';
import { cursoService } from '../../services/cursoService';

export default function DocentesPage() {
  const [docentes, setDocentes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [dispDocente, setDispDocente] = useState(null);
  const [disponibilidades, setDisponibilidades] = useState([]);
  const [showDispForm, setShowDispForm] = useState(false);

  const [compDocente, setCompDocente] = useState(null);
  const [compatibilidades, setCompatibilidades] = useState([]);
  const [cursos, setCursos] = useState([]);
  const [cursoSeleccionado, setCursoSeleccionado] = useState('');

  const recargar = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await docenteService.listar();
      setDocentes(data);
    } catch {
      setError('Error cargando docentes');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { recargar(); }, [recargar]);

  const abrirDisponibilidades = async (doc) => {
    setDispDocente(doc);
    const data = await docenteService.disponibilidades(doc.docenteId);
    setDisponibilidades(data);
  };

  const eliminarDisponibilidad = async (dispId) => {
    await docenteService.eliminarDisponibilidad(dispDocente.docenteId, dispId);
    const data = await docenteService.disponibilidades(dispDocente.docenteId);
    setDisponibilidades(data);
  };

  const abrirCompatibilidades = async (doc) => {
    setCompDocente(doc);
    const [comps, cs] = await Promise.all([
      docenteService.compatibilidades(doc.docenteId),
      cursoService.listar(),
    ]);
    setCompatibilidades(comps);
    setCursos(cs);
    setCursoSeleccionado('');
  };

  const agregarCompatibilidad = async () => {
    if (!cursoSeleccionado) return;
    await docenteService.agregarCompatibilidad(compDocente.docenteId, cursoSeleccionado);
    const data = await docenteService.compatibilidades(compDocente.docenteId);
    setCompatibilidades(data);
    setCursoSeleccionado('');
  };

  const eliminarCompatibilidad = async (cursoId) => {
    await docenteService.eliminarCompatibilidad(compDocente.docenteId, cursoId);
    const data = await docenteService.compatibilidades(compDocente.docenteId);
    setCompatibilidades(data);
  };

  const columns = [
    { key: 'nombreCompleto', label: 'Nombre' },
    { key: 'tipoVinculacion', label: 'Vinculación' },
    { key: 'horasMaxSemana', label: 'Horas máx' },
    { key: 'email', label: 'Email' },
  ];

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Docentes</h1>
      </div>
      {error && <AlertHC variant="error" className="mb-4">{error}</AlertHC>}
      {loading ? <LoadingSpinner /> : (
        <DataTable
          columns={columns}
          data={docentes}
          actions={(row) => (
            <div className="flex gap-1 justify-end">
              <Button size="sm" variant="ghost" onClick={(e) => { e.stopPropagation(); abrirDisponibilidades(row); }}
                title="Ver disponibilidades">📅</Button>
              <Button size="sm" variant="ghost" onClick={(e) => { e.stopPropagation(); abrirCompatibilidades(row); }}
                title="Ver compatibilidades">📚</Button>
            </div>
          )}
        />
      )}

      {dispDocente && (
        <Modal title={`Disponibilidades — ${dispDocente.nombreCompleto}`} onClose={() => setDispDocente(null)}>
          <div className="space-y-3">
            {disponibilidades.length === 0 ? (
              <p className="text-text-secondary text-sm">Sin disponibilidades registradas.</p>
            ) : (
              <table className="w-full text-sm">
                <thead>
                  <tr className="text-text-secondary border-b border-border-color">
                    <th className="py-2 text-left">Día</th>
                    <th className="py-2 text-left">Hora inicio</th>
                    <th className="py-2 text-left">Hora fin</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  {disponibilidades.map(d => (
                    <tr key={d.disponibilidadId} className="border-b border-border-color/40">
                      <td className="py-2 text-text-primary">{d.diaSemana}</td>
                      <td className="py-2 text-text-primary">{d.horaInicio?.slice(0, 5)}</td>
                      <td className="py-2 text-text-primary">{d.horaFin?.slice(0, 5)}</td>
                      <td className="py-2 text-right">
                        <Button size="sm" variant="ghost" onClick={() => eliminarDisponibilidad(d.disponibilidadId)}>🗑️</Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
            <Button variant="secondary" size="sm" onClick={() => setShowDispForm(true)}>+ Agregar disponibilidad</Button>
          </div>
        </Modal>
      )}

      {showDispForm && dispDocente && (
        <DisponibilidadForm
          docenteId={dispDocente.docenteId}
          onClose={() => setShowDispForm(false)}
          onSaved={async () => {
            const data = await docenteService.disponibilidades(dispDocente.docenteId);
            setDisponibilidades(data);
          }}
        />
      )}

      {compDocente && (
        <Modal title={`Compatibilidades — ${compDocente.nombreCompleto}`} onClose={() => setCompDocente(null)}>
          <div className="space-y-4">
            <div className="max-h-72 overflow-y-auto">
              {compatibilidades.length === 0 ? (
                <p className="text-text-secondary text-sm">Sin compatibilidades registradas.</p>
              ) : (
                <table className="w-full text-sm">
                  <thead>
                    <tr className="text-text-secondary border-b border-border-color">
                      <th className="py-2 text-left">Código</th>
                      <th className="py-2 text-left">Curso</th>
                      <th></th>
                    </tr>
                  </thead>
                  <tbody>
                    {compatibilidades.map(c => (
                      <tr key={c.compatibilidadId} className="border-b border-border-color/40 hover:bg-bg-tertiary/50">
                        <td className="py-2 text-text-secondary font-mono text-xs pr-3 whitespace-nowrap">{c.codigoCurso}</td>
                        <td className="py-2 text-text-primary">{c.nombreCurso}</td>
                        <td className="py-2 text-right">
                          <Button size="sm" variant="ghost" onClick={() => eliminarCompatibilidad(c.cursoId)}>🗑️</Button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>
            <div className="flex gap-2 pt-1 border-t border-border-color">
              <select
                value={cursoSeleccionado}
                onChange={e => setCursoSeleccionado(e.target.value)}
                className="flex-1 px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm"
              >
                <option value="">Agregar curso compatible...</option>
                {cursos
                  .filter(c => !compatibilidades.some(cp => cp.cursoId === c.cursoId))
                  .map(c => <option key={c.cursoId} value={c.cursoId}>{c.codigoCurso} — {c.nombreCurso}</option>)
                }
              </select>
              <Button variant="primary" size="sm" onClick={agregarCompatibilidad}>Agregar</Button>
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
}
