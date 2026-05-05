import { useState } from 'react';
import Modal from '../ui/Modal';
import Button from '../ui/Button';
import AlertHC from '../ui/AlertHC';
import { parametroService } from '../../services/parametroService';
import { getErrorMessage } from '../../utils/errors';

export default function ParametroSemestreForm({ parametro, onClose, onSaved }) {
  const [form, setForm] = useState({
    semestre: parametro?.semestre || '',
    franjaInicioLV: parametro?.franjaInicioLV?.slice(0, 5) || '07:00',
    franjaFinLV: parametro?.franjaFinLV?.slice(0, 5) || '22:00',
    franjaInicioSA: parametro?.franjaInicioSA?.slice(0, 5) || '07:00',
    franjaFinSA: parametro?.franjaFinSA?.slice(0, 5) || '13:00',
    exclusionInicio: parametro?.exclusionInicio?.slice(0, 5) || '12:00',
    exclusionFin: parametro?.exclusionFin?.slice(0, 5) || '13:00',
    capMaxGrupo: parametro?.capMaxGrupo || 40,
    umbralCierre: parametro?.umbralCierre || 10,
    freqMaxSesion: parametro?.freqMaxSesion || 4,
    activo: parametro?.activo ?? true,
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const onChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm({ ...form, [name]: type === 'checkbox' ? checked : value });
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      if (parametro?.idParametro) {
        await parametroService.actualizar(parametro.idParametro, form);
      } else {
        await parametroService.crear(form);
      }
      onSaved();
      onClose();
    } catch (err) {
      setError(getErrorMessage(err, 'No se pudo guardar el parámetro.'));
    } finally {
      setLoading(false);
    }
  };

  const inputCls = 'w-full px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm';

  return (
    <Modal title={parametro ? 'Editar parámetro' : 'Nuevo semestre'} onClose={onClose}>
      <form onSubmit={onSubmit} className="space-y-4">
        {error && <AlertHC variant="error">{error}</AlertHC>}
        <div>
          <label className="block text-sm text-text-secondary mb-1">Semestre</label>
          <input name="semestre" value={form.semestre} onChange={onChange} required
            placeholder="2026-1" className={inputCls} />
        </div>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm text-text-secondary mb-1">Lun-vie inicio</label>
            <input name="franjaInicioLV" type="time" value={form.franjaInicioLV} onChange={onChange} className={inputCls} />
          </div>
          <div>
            <label className="block text-sm text-text-secondary mb-1">Lun-vie fin</label>
            <input name="franjaFinLV" type="time" value={form.franjaFinLV} onChange={onChange} className={inputCls} />
          </div>
        </div>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm text-text-secondary mb-1">Sábado inicio</label>
            <input name="franjaInicioSA" type="time" value={form.franjaInicioSA} onChange={onChange} className={inputCls} />
          </div>
          <div>
            <label className="block text-sm text-text-secondary mb-1">Sábado fin</label>
            <input name="franjaFinSA" type="time" value={form.franjaFinSA} onChange={onChange} className={inputCls} />
          </div>
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
        <div className="grid grid-cols-3 gap-4">
          <div>
            <label className="block text-sm text-text-secondary mb-1">Cap. grupo</label>
            <input name="capMaxGrupo" type="number" min="1" value={form.capMaxGrupo} onChange={onChange} className={inputCls} />
          </div>
          <div>
            <label className="block text-sm text-text-secondary mb-1">Umbral cierre</label>
            <input name="umbralCierre" type="number" min="0" value={form.umbralCierre} onChange={onChange} className={inputCls} />
          </div>
          <div>
            <label className="block text-sm text-text-secondary mb-1">Frec. máx.</label>
            <input name="freqMaxSesion" type="number" min="1" max="4" value={form.freqMaxSesion} onChange={onChange} className={inputCls} />
          </div>
        </div>
        <label className="flex items-center gap-2 text-sm text-text-secondary">
          <input name="activo" type="checkbox" checked={form.activo} onChange={onChange} />
          Semestre activo
        </label>
        <div className="flex justify-end gap-3 pt-2">
          <Button variant="secondary" type="button" onClick={onClose}>Cancelar</Button>
          <Button variant="primary" type="submit" loading={loading}>Guardar</Button>
        </div>
      </form>
    </Modal>
  );
}
