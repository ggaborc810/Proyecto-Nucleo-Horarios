import { useState, useCallback, useRef } from 'react';
import { DndContext, closestCenter, DragOverlay } from '@dnd-kit/core';
import CeldaCalendario from './CeldaCalendario';
import BloqueAsignacion from './BloqueAsignacion';
import { useWebSocket } from '../../hooks/useWebSocket';
import { asignacionService } from '../../services/asignacionService';
import { getErrorMessage } from '../../utils/errors';

const DIAS = ['LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO'];
const DIAS_LABEL = { LUNES: 'Lun', MARTES: 'Mar', MIERCOLES: 'Mié', JUEVES: 'Jue', VIERNES: 'Vie', SABADO: 'Sáb' };

function normalizarHora(h) {
  if (!h) return '';
  return h.length === 5 ? h + ':00' : h;
}

export default function CalendarioSemanal({ horario, franjas = [], readOnly = false, onUpdate }) {
  const [activeId, setActiveId] = useState(null);
  const [validacion, setValidacion] = useState({});
  const [error, setError] = useState(null);
  const overCeldaIdRef = useRef(null);

  const onValidacionRecibida = useCallback((data) => {
    const celdaId = overCeldaIdRef.current;
    if (celdaId) {
      setValidacion(prev => ({ ...prev, [celdaId]: data }));
    }
  }, []);

  const { validarMovimiento } = useWebSocket(onValidacionRecibida);

  const asignaciones = horario?.asignaciones || [];

  const mapaAsig = {};
  asignaciones.forEach(a => {
    if (a.diaSemana && a.horaInicio) {
      const key = `${a.diaSemana}_${normalizarHora(a.horaInicio)}`;
      mapaAsig[key] = [...(mapaAsig[key] ?? []), a];
    }
  });

  const horasUnicas = [...new Set(franjas.map(f => f.horaInicio))].sort();

  const activeAsig = asignaciones.find(a => a.idAsignacion === activeId);

  const onDragStart = ({ active }) => {
    setActiveId(active.id);
    setValidacion({});
    setError(null);
    overCeldaIdRef.current = null;
  };

  const onDragOver = ({ over }) => {
    if (!over || readOnly || !activeAsig) return;
    const [dia, ...horaParts] = over.id.split('_');
    const hora = horaParts.join('_');
    const franjaDest = franjas.find(f => f.diaSemana === dia && normalizarHora(f.horaInicio) === hora);
    if (!franjaDest?.franjaId) return;

    overCeldaIdRef.current = over.id;

    validarMovimiento({
      asignacionId: activeAsig.idAsignacion,
      nuevaFranjaId: franjaDest.franjaId,
      nuevaAulaId: activeAsig.aulaId,
    });
  };

  const onDragEnd = async ({ active, over }) => {
    const movida = asignaciones.find(a => a.idAsignacion === active.id);
    setActiveId(null);
    setValidacion({});
    overCeldaIdRef.current = null;

    if (!over || readOnly || !movida) return;
    const [dia, ...horaParts] = over.id.split('_');
    const hora = horaParts.join('_');
    const franjaDest = franjas.find(f => f.diaSemana === dia && normalizarHora(f.horaInicio) === hora);

    if (!franjaDest?.franjaId) {
      setError('No se encontró la franja horaria destino. Verifica que el horario esté generado.');
      return;
    }

    try {
      await asignacionService.mover(movida.idAsignacion, franjaDest.franjaId, movida.aulaId);
      onUpdate?.();
    } catch (err) {
      const hc = err.response?.data?.error;
      const mensaje = getErrorMessage(err, 'No se pudo mover la asignación.');
      setError(hc ? `Movimiento inválido (${hc}): ${mensaje}` : mensaje);
    }
  };

  if (franjas.length === 0) {
    return (
      <div className="text-center py-8 text-text-secondary text-sm">
        No hay franjas horarias cargadas. Genera el horario primero.
      </div>
    );
  }

  return (
    <div>
      {error && (
        <div className="mb-3 px-4 py-2 bg-red-900/30 border border-red-700 text-red-300 rounded-lg text-sm">
          {error}
        </div>
      )}
      <DndContext collisionDetection={closestCenter} onDragStart={onDragStart} onDragOver={onDragOver} onDragEnd={onDragEnd}>
        <div className="bg-bg-secondary rounded-xl border border-border-color overflow-x-auto">
          <table className="w-full min-w-[980px] table-fixed">
            <thead>
              <tr className="bg-bg-tertiary">
                <th className="px-4 py-3 text-left text-xs text-text-secondary w-32">Hora</th>
                {DIAS.map(d => (
                  <th key={d} className="px-2 py-3 text-center text-xs text-text-secondary">{DIAS_LABEL[d]}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {horasUnicas.map(horaInicio => {
                // Referencia para mostrar hora fin (usamos LUNES o cualquier día)
                const ref = franjas.find(f => f.horaInicio === horaInicio);
                return (
                  <tr key={horaInicio} className="border-t border-border-color/40">
                    <td className="px-3 py-2 text-text-secondary text-xs whitespace-nowrap">
                      {horaInicio.slice(0, 5)}–{ref?.horaFin?.slice(0, 5) || ''}
                    </td>
                    {DIAS.map(dia => {
                      const franja = franjas.find(f => f.diaSemana === dia && f.horaInicio === horaInicio);
                      const celdaId = `${dia}_${normalizarHora(horaInicio)}`;
                      const asignacionesCelda = mapaAsig[celdaId] ?? [];
                      return (
                        <CeldaCalendario
                          key={celdaId}
                          id={celdaId}
                          franja={franja}
                          readOnly={readOnly || !franja}
                          validacion={validacion[celdaId]}
                        >
                          {asignacionesCelda.map(asig => (
                            <BloqueAsignacion
                              key={asig.idAsignacion}
                              asignacion={asig}
                              isDragging={activeId === asig.idAsignacion}
                              disabled={readOnly}
                            />
                          ))}
                        </CeldaCalendario>
                      );
                    })}
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
        <DragOverlay>
          {activeAsig && (
            <div className="bg-purple-700 text-white text-xs p-2 rounded-lg shadow-2xl opacity-90 w-40">
              <div className="font-semibold">{activeAsig.nombreCurso}</div>
              <div className="opacity-75">👤 {activeAsig.nombreDocente}</div>
            </div>
          )}
        </DragOverlay>
      </DndContext>
    </div>
  );
}
