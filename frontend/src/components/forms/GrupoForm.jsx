import { useState, useEffect } from 'react';
import Modal from '../ui/Modal';
import Button from '../ui/Button';
import AlertHC from '../ui/AlertHC';
import { grupoService } from '../../services/grupoService';
import { cursoService } from '../../services/cursoService';
import { docenteService } from '../../services/docenteService';
import { getErrorMessage } from '../../utils/errors';

export default function GrupoForm({ grupo, onClose, onSaved }) {
  const [cursos, setCursos] = useState([]);
  const [docentes, setDocentes] = useState([]);
  const [form, setForm] = useState({
    seccion: grupo?.seccion || 'A',
    numInscritos: grupo?.numInscritos || 20,
    semestre: grupo?.semestre || '2026-1',
    estado: grupo?.estado || 'ACTIVO',
    cursoId: grupo?.cursoId || '',
    docenteId: grupo?.docenteId || '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    Promise.all([cursoService.listar(), docenteService.listar()])
      .then(([c, d]) => { setCursos(c); setDocentes(d); })
      .catch(() => {});
  }, []);

  const onChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const onSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      if (grupo?.grupoId) {
        await grupoService.actualizar(grupo.grupoId, form);
      } else {
        await grupoService.crear(form);
      }
      onSaved();
      onClose();
    } catch (err) {
      setError(getErrorMessage(err, 'No se pudo guardar el grupo.'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal title={grupo ? 'Editar Grupo' : 'Nuevo Grupo'} onClose={onClose}>
      <form onSubmit={onSubmit} className="space-y-4">
        {error && <AlertHC variant="error">{error}</AlertHC>}
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm text-text-secondary mb-1">Sección</label>
            <input name="seccion" value={form.seccion} onChange={onChange} required
              className="w-full px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm" />
          </div>
          <div>
            <label className="block text-sm text-text-secondary mb-1">Inscritos</label>
            <input name="numInscritos" type="number" min="1" value={form.numInscritos} onChange={onChange}
              className="w-full px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm" />
          </div>
        </div>
        <div>
          <label className="block text-sm text-text-secondary mb-1">Semestre</label>
          <input name="semestre" value={form.semestre} onChange={onChange} required
            className="w-full px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm" />
        </div>
        <div>
          <label className="block text-sm text-text-secondary mb-1">Curso</label>
          <select name="cursoId" value={form.cursoId} onChange={onChange} required
            className="w-full px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm">
            <option value="">Seleccionar curso...</option>
            {cursos.map(c => <option key={c.cursoId} value={c.cursoId}>{c.nombreCurso}</option>)}
          </select>
        </div>
        <div>
          <label className="block text-sm text-text-secondary mb-1">Docente</label>
          <select name="docenteId" value={form.docenteId} onChange={onChange} required
            className="w-full px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm">
            <option value="">Seleccionar docente...</option>
            {docentes.map(d => <option key={d.docenteId} value={d.docenteId}>{d.nombreCompleto}</option>)}
          </select>
        </div>
        <div className="flex justify-end gap-3 pt-2">
          <Button variant="secondary" type="button" onClick={onClose}>Cancelar</Button>
          <Button variant="primary" type="submit" loading={loading}>Guardar</Button>
        </div>
      </form>
    </Modal>
  );
}
