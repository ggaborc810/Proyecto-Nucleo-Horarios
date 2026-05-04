import api from './api';

export const asignacionService = {
  mover: (id, nuevaFranjaId, nuevaAulaId) =>
    api.put(`/asignaciones/${id}/mover`, { nuevaFranjaId, nuevaAulaId }),
  validar: (asignacionId, nuevaFranjaId, nuevaAulaId) =>
    api.get('/asignaciones/validar-movimiento', {
      params: { asignacionId, nuevaFranjaId, nuevaAulaId },
    }),
};
