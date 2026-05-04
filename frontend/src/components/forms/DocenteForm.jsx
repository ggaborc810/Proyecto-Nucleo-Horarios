import { useState } from 'react';
import Modal from '../ui/Modal';
import Button from '../ui/Button';
import AlertHC from '../ui/AlertHC';
import { docenteService } from '../../services/docenteService';

export default function DocenteForm({ docente, onClose, onSaved }) {
  const [form, setForm] = useState({
    numeroDocumento: docente?.numeroDocumento || '',
    nombreCompleto: docente?.nombreCompleto || '',
    tipoVinculacion: docente?.tipoVinculacion || 'PLANTA',
    horasMaxSemana: docente?.horasMaxSemana || 20,
    email: docente?.email || '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const onChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const onSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      if (docente?.docenteId) {
        await docenteService.actualizar(docente.docenteId, form);
      } else {
        await docenteService.crear(form);
      }
      onSaved();
      onClose();
    } catch (err) {
      setError(err.response?.data?.mensaje || 'Error guardando docente');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal title={docente ? 'Editar Docente' : 'Nuevo Docente'} onClose={onClose}>
      <form onSubmit={onSubmit} className="space-y-4">
        {error && <AlertHC variant="error">{error}</AlertHC>}
        <div>
          <label className="block text-sm text-text-secondary mb-1">Documento</label>
          <input name="numeroDocumento" value={form.numeroDocumento} onChange={onChange} required
            className="w-full px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm" />
        </div>
        <div>
          <label className="block text-sm text-text-secondary mb-1">Nombre completo</label>
          <input name="nombreCompleto" value={form.nombreCompleto} onChange={onChange} required
            className="w-full px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm" />
        </div>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm text-text-secondary mb-1">Vinculación</label>
            <select name="tipoVinculacion" value={form.tipoVinculacion} onChange={onChange}
              className="w-full px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm">
              <option value="PLANTA">Planta</option>
              <option value="CATEDRA">Cátedra</option>
              <option value="HORA_CATEDRA">Hora cátedra</option>
            </select>
          </div>
          <div>
            <label className="block text-sm text-text-secondary mb-1">Horas máx/semana</label>
            <input name="horasMaxSemana" type="number" min="1" max="40" value={form.horasMaxSemana} onChange={onChange}
              className="w-full px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm" />
          </div>
        </div>
        <div>
          <label className="block text-sm text-text-secondary mb-1">Email</label>
          <input name="email" type="email" value={form.email} onChange={onChange}
            className="w-full px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm" />
        </div>
        <div className="flex justify-end gap-3 pt-2">
          <Button variant="secondary" type="button" onClick={onClose}>Cancelar</Button>
          <Button variant="primary" type="submit" loading={loading}>Guardar</Button>
        </div>
      </form>
    </Modal>
  );
}
