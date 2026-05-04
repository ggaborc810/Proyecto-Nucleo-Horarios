import { NavLink, Outlet } from 'react-router-dom';
import TopBar from '../../components/ui/TopBar';

const NAV = [
  { to: '/docente/mi-horario', label: 'Mi Horario' },
  { to: '/docente/mis-grupos', label: 'Mis Grupos' },
  { to: '/docente/disponibilidad', label: 'Disponibilidad' },
];

export default function DocenteLayout() {
  return (
    <div className="min-h-screen bg-bg-primary flex flex-col">
      <TopBar navItems={NAV} homeTo="/docente/mi-horario" roleLabel="Docente" actionLink={{ to: '/horario/2026-1', label: 'Vista publica' }} />

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

      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 py-6 overflow-auto">
        <Outlet />
      </main>
    </div>
  );
}
