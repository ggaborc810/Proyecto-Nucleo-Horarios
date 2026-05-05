import { useState, useEffect, useCallback } from 'react';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import AlertHC from '../../components/ui/AlertHC';
import Button from '../../components/ui/Button';
import Modal from '../../components/ui/Modal';
import { docenteService } from '../../services/docenteService';
import { getErrorMessage } from '../../utils/errors';

const DIAS = ['LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO'];
const DIAS_LABEL = {
  LUNES: 'Lunes', MARTES: 'Martes', MIERCOLES: 'Miércoles',
  JUEVES: 'Jueves', VIERNES: 'Viernes', SABADO: 'Sábado',
};

const FORM_VACIO = { diaSemana: 'LUNES', horaInicio: '07:00', horaFin: '18:00' };

function DisponibilidadModal({ inicial, onClose, onGuardado, docenteId }) {
  const [form, setForm] = useState(inicial ?? FORM_VACIO);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const esEdicion = !!inicial?.disponibilidadId;

  const onChange = (e) => setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));

  const onSubmit = async (e) => {
    e.preventDefault();
    if (form.horaFin <= form.horaInicio) {
      setError('La hora fin debe ser mayor que la hora inicio');
      return;
    }
    setLoading(true);
    setError(null);
    const toTime = (t) => t && t.length === 5 ? t + ':00' : t;
    try {
      const payload = {
        diaSemana: form.diaSemana,
        horaInicio: toTime(form.horaInicio),
        horaFin: toTime(form.horaFin),
      };
      if (esEdicion) {
        await docenteService.actualizarDisponibilidad(docenteId, inicial.disponibilidadId, payload);
      } else {
        await docenteService.agregarDisponibilidad(docenteId, payload);
      }
      onGuardado();
      onClose();
    } catch (err) {
      setError(getErrorMessage(err, 'No se pudo guardar la disponibilidad.'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal title={esEdicion ? 'Editar Disponibilidad' : 'Agregar Disponibilidad'} onClose={onClose} size="sm">
      <form onSubmit={onSubmit} className="space-y-4">
        {error && <AlertHC variant="error">{error}</AlertHC>}
        <div>
          <label className="block text-sm text-text-secondary mb-1">Día</label>
          <select name="diaSemana" value={form.diaSemana} onChange={onChange}
            className="w-full px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm">
            {DIAS.map(d => <option key={d} value={d}>{DIAS_LABEL[d]}</option>)}
          </select>
        </div>
        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="block text-sm text-text-secondary mb-1">Hora inicio</label>
            <input name="horaInicio" type="time" value={form.horaInicio} onChange={onChange} required
              className="w-full px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm" />
          </div>
          <div>
            <label className="block text-sm text-text-secondary mb-1">Hora fin</label>
            <input name="horaFin" type="time" value={form.horaFin} onChange={onChange} required
              className="w-full px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm" />
          </div>
        </div>
        <div className="flex justify-end gap-3 pt-2">
          <Button variant="secondary" type="button" onClick={onClose}>Cancelar</Button>
          <Button variant="primary" type="submit" loading={loading}>
            {esEdicion ? 'Guardar cambios' : 'Agregar'}
          </Button>
        </div>
      </form>
    </Modal>
  );
}

export default function DisponibilidadPage() {
  const [disponibilidades, setDisponibilidades] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modal, setModal] = useState(null); // null | { disponibilidad } para edición | 'nuevo'
  const [error, setError] = useState(null);

  const docenteId = localStorage.getItem('docenteId');

  const recargar = useCallback(async () => {
    if (!docenteId) return;
    setLoading(true);
    setError(null);
    try {
      const data = await docenteService.disponibilidades(docenteId);
      setDisponibilidades(data);
    } catch {
      setError('Error cargando disponibilidades');
    } finally {
      setLoading(false);
    }
  }, [docenteId]);

  useEffect(() => { recargar(); }, [recargar]);

  const eliminar = async (dispId) => {
    if (!confirm('¿Eliminar esta franja de disponibilidad?')) return;
    setError(null);
    try {
      await docenteService.eliminarDisponibilidad(docenteId, dispId);
      recargar();
    } catch {
      setError('Error eliminando disponibilidad');
    }
  };

  if (!docenteId) {
    return <AlertHC variant="error">No se encontró tu identificador de docente.</AlertHC>;
  }

  const agrupadoPorDia = DIAS.reduce((acc, dia) => {
    acc[dia] = disponibilidades.filter(d => d.diaSemana === dia);
    return acc;
  }, {});

  const tieneAlgunDia = DIAS.some(dia => agrupadoPorDia[dia].length > 0);

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-2xl font-bold text-text-primary">Disponibilidad Horaria</h1>
          <p className="text-text-secondary text-sm mt-1">
            Franjas en que puedes dictar clase. El sistema solo te asignará sesiones dentro de estos bloques.
          </p>
        </div>
        <Button variant="primary" onClick={() => setModal('nuevo')}>+ Agregar franja</Button>
      </div>

      {error && <AlertHC variant="error" className="mb-4">{error}</AlertHC>}

      {loading ? (
        <LoadingSpinner />
      ) : !tieneAlgunDia ? (
        <div className="bg-bg-secondary border border-border-color rounded-xl p-8 text-center">
          <div className="text-4xl mb-3">📅</div>
          <h3 className="text-lg font-semibold text-text-primary mb-1">Sin disponibilidad registrada</h3>
          <p className="text-text-secondary text-sm mb-4">
            Agrega al menos una franja para que el sistema pueda asignarte sesiones en el horario.
          </p>
          <Button variant="primary" onClick={() => setModal('nuevo')}>Agregar primera franja</Button>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {DIAS.filter(dia => agrupadoPorDia[dia].length > 0).map(dia => (
            <div key={dia} className="bg-bg-secondary border border-border-color rounded-xl p-4">
              <div className="font-semibold text-text-primary mb-3 text-sm uppercase tracking-wide">
                {DIAS_LABEL[dia]}
              </div>
              <div className="space-y-2">
                {agrupadoPorDia[dia].map(d => (
                  <div key={d.disponibilidadId}
                    className="flex items-center justify-between bg-bg-tertiary rounded-lg px-3 py-2">
                    <span className="text-sm text-text-primary font-mono">
                      {d.horaInicio?.slice(0, 5)} – {d.horaFin?.slice(0, 5)}
                    </span>
                    <div className="flex gap-2 text-xs">
                      <button
                        onClick={() => setModal(d)}
                        className="text-accent-blue hover:text-blue-400 transition"
                      >
                        Editar
                      </button>
                      <span className="text-border-color">|</span>
                      <button
                        onClick={() => eliminar(d.disponibilidadId)}
                        className="text-accent-red hover:text-red-400 transition"
                      >
                        Eliminar
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      )}

      {modal !== null && (
        <DisponibilidadModal
          inicial={modal === 'nuevo' ? null : {
            disponibilidadId: modal.disponibilidadId,
            diaSemana: modal.diaSemana,
            horaInicio: modal.horaInicio?.slice(0, 5),
            horaFin: modal.horaFin?.slice(0, 5),
          }}
          docenteId={docenteId}
          onClose={() => setModal(null)}
          onGuardado={recargar}
        />
      )}
    </div>
  );
}
