import api from './api';

export const parametroService = {
  listar: () => api.get('/parametros'),
  activo: () => api.get('/parametros/activo'),
  crear: (dto) => api.post('/parametros', dto),
  actualizar: (id, dto) => api.put(`/parametros/${id}`, dto),
};
