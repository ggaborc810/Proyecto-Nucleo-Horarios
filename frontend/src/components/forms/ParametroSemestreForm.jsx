import { useState } from 'react';
import Modal from '../ui/Modal';
import Button from '../ui/Button';
import AlertHC from '../ui/AlertHC';
import { parametroService } from '../../services/parametroService';

export default function ParametroSemestreForm({ parametro, onClose, onSaved }) {
  const [form, setForm] = useState({
    semestre: parametro?.semestre || '',
    fechaInicioSemestre: parametro?.fechaInicioSemestre || '',
    fechaFinSemestre: parametro?.fechaFinSemestre || '',
    horaInicioJornada: parametro?.horaInicioJornada?.slice(0, 5) || '07:00',
    horaFinJornada: parametro?.horaFinJornada?.slice(0, 5) || '22:00',
    duracionSesionMinutos: parametro?.duracionSesionMinutos || 120,
    exclusionInicio: parametro?.exclusionInicio?.slice(0, 5) || '12:00',
    exclusionFin: parametro?.exclusionFin?.slice(0, 5) || '13:00',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const onChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const onSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      if (parametro?.parametroId) {
        await parametroService.actualizar(parametro.parametroId, form);
      } else {
        await parametroService.crear(form);
      }
      onSaved();
      onClose();
    } catch (err) {
      setError(err.response?.data?.mensaje || 'Error guardando parámetro');
    } finally {
      setLoading(false);
    }
  };

  const inputCls = 'w-full px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm';

  return (
    <Modal title={parametro ? 'Editar Parámetro' : 'Nuevo Semestre'} onClose={onClose}>
      <form onSubmit={onSubmit} className="space-y-4">
        {error && <AlertHC variant="error">{error}</AlertHC>}
        <div>
          <label className="block text-sm text-text-secondary mb-1">Semestre (ej. 2026-1)</label>
          <input name="semestre" value={form.semestre} onChange={onChange} required
            placeholder="2026-1" className={inputCls} />
        </div>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm text-text-secondary mb-1">Fecha inicio</label>
            <input name="fechaInicioSemestre" type="date" value={form.fechaInicioSemestre} onChange={onChange} required className={inputCls} />
          </div>
          <div>
            <label className="block text-sm text-text-secondary mb-1">Fecha fin</label>
            <input name="fechaFinSemestre" type="date" value={form.fechaFinSemestre} onChange={onChange} required className={inputCls} />
          </div>
        </div>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm text-text-secondary mb-1">Hora inicio jornada</label>
            <input name="horaInicioJornada" type="time" value={form.horaInicioJornada} onChange={onChange} className={inputCls} />
          </div>
          <div>
            <label className="block text-sm text-text-secondary mb-1">Hora fin jornada</label>
            <input name="horaFinJornada" type="time" value={form.horaFinJornada} onChange={onChange} className={inputCls} />
          </div>
        </div>
        <div>
          <label className="block text-sm text-text-secondary mb-1">Duración sesión (minutos)</label>
          <input name="duracionSesionMinutos" type="number" min="60" max="240" step="30"
            value={form.duracionSesionMinutos} onChange={onChange} className={inputCls} />
        </div>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm text-text-secondary mb-1">Exclusión inicio</label>
            <input name="exclusionInicio" type="time" value={form.exclusionInicio} onChange={onChange} className={inputCls} />
          </div>
          <div>
            <label className="block text-sm text-text-secondary mb-1">Exclusión fin</label>
            <input name="exclusionFin" type="time" value={form.exclusionFin} onChange={onChange} className={inputCls} />
          </div>
        </div>
        <div className="flex justify-end gap-3 pt-2">
          <Button variant="secondary" type="button" onClick={onClose}>Cancelar</Button>
          <Button variant="primary" type="submit" loading={loading}>Guardar</Button>
        </div>
      </form>
    </Modal>
  );
}
