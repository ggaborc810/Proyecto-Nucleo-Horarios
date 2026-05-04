import { useState, useEffect } from 'react';
import Modal from '../ui/Modal';
import Button from '../ui/Button';
import AlertHC from '../ui/AlertHC';
import { cursoService } from '../../services/cursoService';
import { aulaService } from '../../services/aulaService';

export default function CursoForm({ curso, onClose, onSaved }) {
  const [tipos, setTipos] = useState([]);
  const [form, setForm] = useState({
    codigoCurso: curso?.codigoCurso || '',
    nombreCurso: curso?.nombreCurso || '',
    creditosAcademicos: curso?.creditosAcademicos || 3,
    horasSemanales: curso?.horasSemanales || 4,
    frecuenciaSemanal: curso?.frecuenciaSemanal || 2,
    semestreNivel: curso?.semestreNivel || 1,
    idTipoAulaRequerida: curso?.idTipoAulaRequerida || '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => { aulaService.tiposAula().then(setTipos).catch(() => {}); }, []);

  const onChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const onSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      if (curso?.cursoId) {
        await cursoService.actualizar(curso.cursoId, form);
      } else {
        await cursoService.crear(form);
      }
      onSaved();
      onClose();
    } catch (err) {
      setError(err.response?.data?.mensaje || 'Error guardando curso');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal title={curso ? 'Editar Curso' : 'Nuevo Curso'} onClose={onClose}>
      <form onSubmit={onSubmit} className="space-y-4">
        {error && <AlertHC variant="error">{error}</AlertHC>}
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm text-text-secondary mb-1">Código</label>
            <input name="codigoCurso" value={form.codigoCurso} onChange={onChange} required
              className="w-full px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm" />
          </div>
          <div>
            <label className="block text-sm text-text-secondary mb-1">Semestre nivel</label>
            <input name="semestreNivel" type="number" min="1" max="10" value={form.semestreNivel} onChange={onChange}
              className="w-full px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm" />
          </div>
        </div>
        <div>
          <label className="block text-sm text-text-secondary mb-1">Nombre del curso</label>
          <input name="nombreCurso" value={form.nombreCurso} onChange={onChange} required
            className="w-full px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm" />
        </div>
        <div className="grid grid-cols-3 gap-4">
          <div>
            <label className="block text-sm text-text-secondary mb-1">Créditos</label>
            <input name="creditosAcademicos" type="number" min="1" value={form.creditosAcademicos} onChange={onChange}
              className="w-full px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm" />
          </div>
          <div>
            <label className="block text-sm text-text-secondary mb-1">Horas/semana</label>
            <input name="horasSemanales" type="number" min="1" value={form.horasSemanales} onChange={onChange}
              className="w-full px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm" />
          </div>
          <div>
            <label className="block text-sm text-text-secondary mb-1">Sesiones/semana</label>
            <input name="frecuenciaSemanal" type="number" min="1" max="5" value={form.frecuenciaSemanal} onChange={onChange}
              className="w-full px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm" />
          </div>
        </div>
        <div>
          <label className="block text-sm text-text-secondary mb-1">Tipo de aula requerida</label>
          <select name="idTipoAulaRequerida" value={form.idTipoAulaRequerida} onChange={onChange} required
            className="w-full px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm">
            <option value="">Seleccionar...</option>
            {tipos.map(t => <option key={t.idTipoAula} value={t.idTipoAula}>{t.nombre}</option>)}
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
