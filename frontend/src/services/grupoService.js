import api from './api';

export const grupoService = {
  listar: (estado) => api.get('/grupos', { params: estado ? { estado } : {} }),
  obtener: (id) => api.get(`/grupos/${id}`),
  crear: (dto) => api.post('/grupos', dto),
  actualizar: (id, dto) => api.put(`/grupos/${id}`, dto),
  cerrar: (id) => api.put(`/grupos/${id}/cerrar`, {}),
  reabrir: (id) => api.put(`/grupos/${id}/reabrir`),
  cerrarAutomatico: () => api.post('/grupos/cerrar-automatico'),
  listarPublicos: () => api.get('/publico/grupos'),
  obtenerPublico: (id) => api.get(`/publico/grupos/${id}`),
};
