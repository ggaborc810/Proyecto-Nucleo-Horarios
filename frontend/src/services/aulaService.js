import api from './api';

export const aulaService = {
  listar: () => api.get('/aulas'),
  obtener: (id) => api.get(`/aulas/${id}`),
  crear: (dto) => api.post('/aulas', dto),
  actualizar: (id, dto) => api.put(`/aulas/${id}`, dto),
  tiposAula: () => api.get('/tipos-aula'),
};
