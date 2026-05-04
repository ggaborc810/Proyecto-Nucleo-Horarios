import api from './api';

export const cursoService = {
  listar: () => api.get('/cursos'),
  obtener: (id) => api.get(`/cursos/${id}`),
  crear: (dto) => api.post('/cursos', dto),
  actualizar: (id, dto) => api.put(`/cursos/${id}`, dto),
  eliminar: (id) => api.delete(`/cursos/${id}`),
};
