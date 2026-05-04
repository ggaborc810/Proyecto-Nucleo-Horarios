import { NavLink } from 'react-router-dom';

const items = [
  { to: '/admin',            label: 'Dashboard',   icon: '▦' },
  { to: '/admin/horario',    label: 'Horario',      icon: '◫' },
  { to: '/admin/docentes',   label: 'Docentes',     icon: '◉' },
  { to: '/admin/aulas',      label: 'Aulas',        icon: '⬜' },
  { to: '/admin/cursos',     label: 'Cursos',       icon: '◈' },
  { to: '/admin/grupos',     label: 'Grupos',       icon: '⊞' },
  { to: '/admin/parametros', label: 'Parámetros',   icon: '⚙' },
];

export default function Sidebar() {
  return (
    <aside className="w-60 bg-bg-secondary border-r border-border-color min-h-screen flex flex-col shrink-0">
      <div className="p-5 border-b border-border-color">
        <h1 className="text-lg font-bold text-text-primary">Horarios UEB</h1>
        <p className="text-xs text-text-muted mt-0.5">Ing. de Sistemas</p>
      </div>
      <nav className="flex-1 p-3 space-y-0.5">
        {items.map(it => (
          <NavLink
            key={it.to}
            to={it.to}
            end={it.to === '/admin'}
            className={({ isActive }) =>
              `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition ${
                isActive
                  ? 'bg-accent-blue text-white font-medium'
                  : 'text-text-secondary hover:bg-bg-tertiary hover:text-text-primary'
              }`
            }
          >
            <span className="text-base w-5 text-center">{it.icon}</span>
            <span>{it.label}</span>
          </NavLink>
        ))}
      </nav>
      <div className="p-4 border-t border-border-color">
        <a href="/horario/2026-1" target="_blank" rel="noreferrer"
          className="text-xs text-text-muted hover:text-accent-blue transition">
          ↗ Vista pública
        </a>
      </div>
    </aside>
  );
}
