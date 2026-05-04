import { Navigate, Outlet } from 'react-router-dom';

export default function PrivateRoute({ roles }) {
  const token = localStorage.getItem('jwt');
  const rol = localStorage.getItem('rol');

  if (!token) return <Navigate to="/login" replace />;
  if (roles && !roles.includes(rol)) return <Navigate to="/" replace />;

  return <Outlet />;
}
