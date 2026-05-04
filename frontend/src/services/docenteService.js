import api from './api';

export const docenteService = {
  listar: () => api.get('/docentes'),
  obtener: (id) => api.get(`/docentes/${id}`),
  crear: (dto) => api.post('/docentes', dto),
  actualizar: (id, dto) => api.put(`/docentes/${id}`, dto),
  eliminar: (id) => api.delete(`/docentes/${id}`),
  disponibilidades: (id) => api.get(`/docentes/${id}/disponibilidad`),
  agregarDisponibilidad: (id, dto) => api.post(`/docentes/${id}/disponibilidad`, dto),
  actualizarDisponibilidad: (id, dispId, dto) => api.put(`/docentes/${id}/disponibilidad/${dispId}`, dto),
  eliminarDisponibilidad: (id, dispId) => api.delete(`/docentes/${id}/disponibilidad/${dispId}`),
  compatibilidades: (id) => api.get(`/docentes/${id}/compatibilidades`),
  agregarCompatibilidad: (id, cursoId) => api.post(`/docentes/${id}/compatibilidades`, { cursoId }),
  eliminarCompatibilidad: (id, cursoId) => api.delete(`/docentes/${id}/compatibilidades/${cursoId}`),
};
