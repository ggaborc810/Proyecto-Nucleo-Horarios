import api from './api';

export const horarioService = {
  generar: (semestre, cursoIds) => api.post('/horarios/generar', { semestre, cursoIds }),
  obtener: (semestre) => api.get(`/horarios/${semestre}`),
  publicar: (id) => api.put(`/horarios/${id}/publicar`),
  conflictos: (semestre) => api.get(`/horarios/${semestre}/conflictos`),
  obtenerPublico: (semestre) => api.get(`/publico/horario/${semestre}`),
  obtenerPublicoPorDocente: (semestre, docenteId) =>
    api.get(`/publico/horario/${semestre}/docente/${docenteId}`),
  obtenerBorrador: (semestre) => api.get(`/horarios/${semestre}`),
};
