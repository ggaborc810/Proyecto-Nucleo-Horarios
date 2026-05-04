import { useEffect, useCallback } from 'react';
import { conectar, validarMovimiento as wsValidar, desconectar } from '../websocket/stompClient';

export function useWebSocket(onValidacion) {
  useEffect(() => {
    conectar(onValidacion);
    return () => desconectar();
  }, [onValidacion]);

  const validarMovimiento = useCallback((movimiento) => {
    wsValidar(movimiento);
  }, []);

  return { validarMovimiento };
}
