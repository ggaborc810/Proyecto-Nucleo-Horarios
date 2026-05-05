import { useState, useEffect, useCallback } from 'react';
import { horarioService } from '../services/horarioService';
import api from '../services/api';
import { getErrorMessage } from '../utils/errors';

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
      setError(getErrorMessage(err, 'No se pudo cargar el horario.'));
    } finally {
      setLoading(false);
    }
  }, [semestre]);

  useEffect(() => { recargar(); }, [recargar]);

  return { horario, franjas, loading, error, recargar };
}
