import { useState, useEffect, useCallback } from 'react';
import { horarioService } from '../services/horarioService';
import api from '../services/api';

export function useCalendario(semestre) {
  const [horario, setHorario] = useState(null);
  const [franjas, setFranjas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const recargar = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [h, f] = await Promise.all([
        horarioService.obtener(semestre),
        api.get('/parametros/activo/franjas'),
      ]);
      setHorario(h);
      setFranjas(f.data ?? f);
    } catch (err) {
      setError(err.response?.data?.mensaje || 'Error cargando horario');
    } finally {
      setLoading(false);
    }
  }, [semestre]);

  useEffect(() => { recargar(); }, [recargar]);

  return { horario, franjas, loading, error, recargar };
}
