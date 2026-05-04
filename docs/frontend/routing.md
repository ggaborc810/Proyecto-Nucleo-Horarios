# Routing — React Router 6

## Esquema de Rutas

| Ruta | Acceso | Vista |
|------|--------|-------|
| `/` | Público | Redirige a `/horario/:semestre-actual` |
| `/horario/:semestre` | Público | Vista pública del horario |
| `/login` | Sin auth | Login |
| `/admin` | ADMIN | Dashboard |
| `/admin/horario` | ADMIN | Calendario con drag-and-drop |
| `/admin/horario/generar` | ADMIN | Generar horario + conflictos |
| `/admin/docentes` | ADMIN | CRUD docentes |
| `/admin/aulas` | ADMIN | CRUD aulas |
| `/admin/cursos` | ADMIN | CRUD cursos |
| `/admin/grupos` | ADMIN | Gestión de grupos |
| `/admin/parametros` | ADMIN | Configurar `ParametroSemestre` |
| `/mi-horario` | DOCENTE | Vista del docente autenticado |

## App.jsx

```jsx
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import PrivateRoute from './components/ui/PrivateRoute';

import LoginPage from './views/LoginPage';
import AdminLayout from './views/admin/AdminLayout';
import DashboardPage from './views/admin/DashboardPage';
import CalendarioPage from './views/admin/CalendarioPage';
import HorarioGenerarPage from './views/admin/HorarioGenerarPage';
import DocentesPage from './views/admin/DocentesPage';
import AulasPage from './views/admin/AulasPage';
import CursosPage from './views/admin/CursosPage';
import GruposPage from './views/admin/GruposPage';
import ParametrosPage from './views/admin/ParametrosPage';
import MiHorarioPage from './views/docente/MiHorarioPage';
import PublicoLayout from './views/publico/PublicoLayout';
import HorarioPublicoPage from './views/publico/HorarioPublicoPage';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Público */}
        <Route element={<PublicoLayout />}>
          <Route path="/" element={<Navigate to="/horario/2026-1" replace />} />
          <Route path="/horario/:semestre" element={<HorarioPublicoPage />} />
        </Route>

        {/* Auth */}
        <Route path="/login" element={<LoginPage />} />

        {/* Admin */}
        <Route element={<PrivateRoute roles={['ADMIN']} />}>
          <Route path="/admin" element={<AdminLayout />}>
            <Route index element={<DashboardPage />} />
            <Route path="horario" element={<CalendarioPage />} />
            <Route path="horario/generar" element={<HorarioGenerarPage />} />
            <Route path="docentes" element={<DocentesPage />} />
            <Route path="aulas" element={<AulasPage />} />
            <Route path="cursos" element={<CursosPage />} />
            <Route path="grupos" element={<GruposPage />} />
            <Route path="parametros" element={<ParametrosPage />} />
          </Route>
        </Route>

        {/* Docente */}
        <Route element={<PrivateRoute roles={['DOCENTE']} />}>
          <Route path="/mi-horario" element={<MiHorarioPage />} />
        </Route>

        {/* Catch-all */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
```

## PrivateRoute

```jsx
import { Navigate, Outlet } from 'react-router-dom';

export default function PrivateRoute({ roles }) {
  const token = localStorage.getItem('jwt');
  const rol = localStorage.getItem('rol');

  if (!token) return <Navigate to="/login" replace />;
  if (roles && !roles.includes(rol)) return <Navigate to="/" replace />;

  return <Outlet />;
}
```

## Hook useAuth

```js
import { useState, useEffect } from 'react';

export function useAuth() {
  const [usuario, setUsuario] = useState(() => {
    const token = localStorage.getItem('jwt');
    const rol = localStorage.getItem('rol');
    return token ? { token, rol } : null;
  });

  const login = (token, rol) => {
    localStorage.setItem('jwt', token);
    localStorage.setItem('rol', rol);
    setUsuario({ token, rol });
  };

  const logout = () => {
    localStorage.removeItem('jwt');
    localStorage.removeItem('rol');
    setUsuario(null);
    window.location.href = '/login';
  };

  return { usuario, login, logout, autenticado: !!usuario };
}
```

## Comportamiento de Acceso

- Sin JWT en `/admin/*` → redirige a `/login`
- JWT con rol DOCENTE en `/admin/*` → redirige a `/`
- 401 desde el backend (token expirado) → interceptor de axios limpia localStorage y redirige a `/login`
- Vista pública es accesible siempre, incluso autenticado como ADMIN
