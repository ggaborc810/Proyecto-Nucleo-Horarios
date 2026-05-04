import { useState } from 'react';

export function useAuth() {
  const [usuario, setUsuario] = useState(() => {
    const token = localStorage.getItem('jwt');
    const rol = localStorage.getItem('rol');
    const username = localStorage.getItem('username');
    return token ? { token, rol, username } : null;
  });

  const login = (token, rol, username) => {
    localStorage.setItem('jwt', token);
    localStorage.setItem('rol', rol);
    localStorage.setItem('username', username || '');
    setUsuario({ token, rol, username });
  };

  const logout = () => {
    localStorage.removeItem('jwt');
    localStorage.removeItem('rol');
    localStorage.removeItem('username');
    setUsuario(null);
    window.location.href = '/login';
  };

  return { usuario, login, logout, autenticado: !!usuario };
}
