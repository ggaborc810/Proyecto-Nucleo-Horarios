import { useState, useEffect } from 'react';
import Modal from '../ui/Modal';
import Button from '../ui/Button';
import AlertHC from '../ui/AlertHC';
import { aulaService } from '../../services/aulaService';

export default function AulaForm({ aula, onClose, onSaved }) {
  const [tipos, setTipos] = useState([]);
  const [form, setForm] = useState({
    codigoAula: aula?.codigoAula || '',
    nombre: aula?.nombre || '',
    capacidad: aula?.capacidad || 30,
    idTipoAula: aula?.idTipoAula || '',
    activa: aula?.activa ?? true,
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    aulaService.tiposAula().then(setTipos).catch(() => {});
  }, []);

  const onChange = (e) => {
    const val = e.target.type === 'checkbox' ? e.target.checked : e.target.value;
    setForm({ ...form, [e.target.name]: val });
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      if (aula?.aulaId) {
        await aulaService.actualizar(aula.aulaId, form);
      } else {
        await aulaService.crear(form);
      }
      onSaved();
      onClose();
    } catch (err) {
      setError(err.response?.data?.mensaje || 'Error guardando aula');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal title={aula ? 'Editar Aula' : 'Nueva Aula'} onClose={onClose} size="sm">
      <form onSubmit={onSubmit} className="space-y-4">
        {error && <AlertHC variant="error">{error}</AlertHC>}
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm text-text-secondary mb-1">Código</label>
            <input name="codigoAula" value={form.codigoAula} onChange={onChange} required
              className="w-full px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm" />
          </div>
          <div>
            <label className="block text-sm text-text-secondary mb-1">Capacidad</label>
            <input name="capacidad" type="number" min="1" value={form.capacidad} onChange={onChange} required
              className="w-full px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm" />
          </div>
        </div>
        <div>
          <label className="block text-sm text-text-secondary mb-1">Nombre</label>
          <input name="nombre" value={form.nombre} onChange={onChange}
            className="w-full px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm" />
        </div>
        <div>
          <label className="block text-sm text-text-secondary mb-1">Tipo de aula</label>
          <select name="idTipoAula" value={form.idTipoAula} onChange={onChange} required
            className="w-full px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm">
            <option value="">Seleccionar...</option>
            {tipos.map(t => <option key={t.idTipoAula} value={t.idTipoAula}>{t.nombre}</option>)}
          </select>
        </div>
        <label className="flex items-center gap-2 text-sm text-text-secondary cursor-pointer">
          <input type="checkbox" name="activa" checked={form.activa} onChange={onChange} className="w-4 h-4" />
          Activa
        </label>
        <div className="flex justify-end gap-3 pt-2">
          <Button variant="secondary" type="button" onClick={onClose}>Cancelar</Button>
          <Button variant="primary" type="submit" loading={loading}>Guardar</Button>
        </div>
      </form>
    </Modal>
  );
}
