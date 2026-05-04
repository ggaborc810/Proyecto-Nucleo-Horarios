import { useState } from 'react';
import Modal from '../ui/Modal';
import Button from '../ui/Button';
import AlertHC from '../ui/AlertHC';
import { docenteService } from '../../services/docenteService';

const DIAS = ['LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO'];

export default function DisponibilidadForm({ docenteId, onClose, onSaved }) {
  const [form, setForm] = useState({ diaSemana: 'LUNES', horaInicio: '07:00', horaFin: '18:00' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const onChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const onSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      await docenteService.agregarDisponibilidad(docenteId, form);
      onSaved();
      onClose();
    } catch (err) {
      setError(err.response?.data?.mensaje || 'Error guardando disponibilidad');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal title="Agregar Disponibilidad" onClose={onClose} size="sm">
      <form onSubmit={onSubmit} className="space-y-4">
        {error && <AlertHC variant="error">{error}</AlertHC>}
        <div>
          <label className="block text-sm text-text-secondary mb-1">Día</label>
          <select name="diaSemana" value={form.diaSemana} onChange={onChange}
            className="w-full px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm">
            {DIAS.map(d => <option key={d} value={d}>{d}</option>)}
          </select>
        </div>
        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="block text-sm text-text-secondary mb-1">Hora inicio</label>
            <input name="horaInicio" type="time" value={form.horaInicio} onChange={onChange}
              className="w-full px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm" />
          </div>
          <div>
            <label className="block text-sm text-text-secondary mb-1">Hora fin</label>
            <input name="horaFin" type="time" value={form.horaFin} onChange={onChange}
              className="w-full px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm" />
          </div>
        </div>
        <div className="flex justify-end gap-3 pt-2">
          <Button variant="secondary" type="button" onClick={onClose}>Cancelar</Button>
          <Button variant="primary" type="submit" loading={loading}>Agregar</Button>
        </div>
      </form>
    </Modal>
  );
}
