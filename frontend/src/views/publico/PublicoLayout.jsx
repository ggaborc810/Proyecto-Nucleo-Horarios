import { NavLink, Outlet, useLocation } from 'react-router-dom';
import TopBar from '../../components/ui/TopBar';

const NAV = [
  { to: '/horario/2026-1', label: 'Horario' },
  { to: '/grupos', label: 'Grupos' },
];

export default function PublicoLayout() {
  const location = useLocation();
  const rol = localStorage.getItem('rol');
  const roleLabel = rol === 'ADMIN' ? 'Admin' : rol === 'DOCENTE' ? 'Docente' : rol === 'ESTUDIANTE' ? 'Estudiante' : null;
  const estudianteEnHorario = rol === 'ESTUDIANTE' && location.pathname.startsWith('/horario/');
  const actionLink = rol === 'ADMIN'
    ? { to: '/admin', label: 'Panel Admin' }
    : rol === 'DOCENTE'
      ? { to: '/docente', label: 'Mi Panel' }
      : null;

  return (
    <div className="min-h-screen bg-bg-primary flex flex-col">
      <TopBar navItems={NAV} homeTo="/horario/2026-1" roleLabel={roleLabel} actionLink={actionLink} />

      <nav className="md:hidden flex gap-1 overflow-x-auto px-4 py-2 bg-bg-secondary border-b border-border-color shrink-0">
        {NAV.map(({ to, label }) => (
          <NavLink
            key={to}
            to={to}
            className={({ isActive }) =>
              `shrink-0 px-3 py-1.5 rounded-lg text-xs transition ${
                isActive
                  ? 'bg-accent-blue/20 text-accent-blue font-medium'
                  : 'text-text-secondary hover:text-text-primary'
              }`
            }
          >
            {label}
          </NavLink>
        ))}
      </nav>

      <main className={`flex-1 w-full px-4 sm:px-6 py-6 overflow-auto ${estudianteEnHorario ? '' : 'max-w-7xl mx-auto'}`}>
        <Outlet />
      </main>
    </div>
  );
}
