import { Link, NavLink, useNavigate } from 'react-router-dom';
import Button from './Button';

export default function TopBar({ navItems = [], homeTo = '/', roleLabel, actionLink }) {
  const navigate = useNavigate();
  const jwt = localStorage.getItem('jwt');
  const username = localStorage.getItem('username') || roleLabel || 'Usuario';

  const logout = () => {
    ['jwt', 'rol', 'username', 'docenteId'].forEach(k => localStorage.removeItem(k));
    navigate('/login', { replace: true });
  };

  return (
    <header className="h-16 bg-bg-secondary border-b border-border-color flex items-center justify-between px-4 sm:px-6 shrink-0">
      <div className="flex items-center gap-4 min-w-0">
        <Link to={homeTo} className="shrink-0">
          <div className="text-base font-bold text-text-primary leading-tight">Horarios UEB</div>
          <div className="text-xs text-text-muted leading-tight">Ing. de Sistemas</div>
        </Link>
        {navItems.length > 0 && (
          <nav className="hidden md:flex items-center gap-1">
            {navItems.map(({ to, label, end }) => (
              <NavLink
                key={to}
                to={to}
                end={end}
                className={({ isActive }) =>
                  `px-3 py-1.5 rounded-lg text-sm transition ${
                    isActive
                      ? 'bg-accent-blue/20 text-accent-blue font-medium'
                      : 'text-text-secondary hover:text-text-primary hover:bg-bg-tertiary'
                  }`
                }
              >
                {label}
              </NavLink>
            ))}
          </nav>
        )}
      </div>

      <div className="flex items-center gap-3 shrink-0">
        {actionLink && (
          <Link to={actionLink.to} className="hidden sm:inline text-sm text-accent-blue hover:text-blue-400 transition">
            {actionLink.label}
          </Link>
        )}
        {jwt ? (
          <>
            <span className="hidden sm:block text-sm text-text-secondary">
              {username}{roleLabel ? <span className="text-xs opacity-60"> ({roleLabel})</span> : null}
            </span>
            <Button variant="ghost" size="sm" onClick={logout}>Salir</Button>
          </>
        ) : (
          <Link to="/login" className="text-sm text-accent-blue hover:text-blue-400 font-medium transition">
            Acceso
          </Link>
        )}
      </div>
    </header>
  );
}
