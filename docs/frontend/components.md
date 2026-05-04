# Estructura de Componentes

## Árbol Completo

```
src/
├── App.jsx                           # Router principal
├── main.jsx                          # Entry point
├── index.css                         # Variables CSS + Tailwind
│
├── views/                            # Páginas (rutas)
│   ├── LoginPage.jsx
│   ├── admin/
│   │   ├── AdminLayout.jsx           # Sidebar + topbar
│   │   ├── DashboardPage.jsx
│   │   ├── HorarioGenerarPage.jsx
│   │   ├── CalendarioPage.jsx
│   │   ├── DocentesPage.jsx
│   │   ├── AulasPage.jsx
│   │   ├── CursosPage.jsx
│   │   ├── GruposPage.jsx
│   │   └── ParametrosPage.jsx
│   ├── docente/
│   │   └── MiHorarioPage.jsx
│   └── publico/
│       ├── PublicoLayout.jsx
│       └── HorarioPublicoPage.jsx
│
├── components/
│   ├── ui/                           # Componentes genéricos reutilizables
│   │   ├── Button.jsx
│   │   ├── Badge.jsx
│   │   ├── Modal.jsx
│   │   ├── Sidebar.jsx
│   │   ├── TopBar.jsx
│   │   ├── LoadingSpinner.jsx
│   │   ├── AlertHC.jsx
│   │   ├── FilterBar.jsx
│   │   └── PrivateRoute.jsx
│   ├── tables/
│   │   ├── DataTable.jsx
│   │   └── ConflictosTable.jsx
│   ├── forms/
│   │   ├── DocenteForm.jsx
│   │   ├── DisponibilidadForm.jsx
│   │   ├── AulaForm.jsx
│   │   ├── CursoForm.jsx
│   │   ├── GrupoForm.jsx
│   │   └── ParametroSemestreForm.jsx
│   └── calendar/                     # Ver docs/frontend/calendar.md
│       ├── CalendarioSemanal.jsx
│       ├── CeldaCalendario.jsx
│       ├── BloqueAsignacion.jsx
│       └── ConflictoPanel.jsx
│
├── hooks/
│   ├── useAuth.js
│   ├── useHorario.js
│   ├── useCalendario.js
│   └── useWebSocket.js
│
├── services/                         # Clientes HTTP (axios)
│   ├── api.js                        # Instancia base + interceptores
│   ├── authService.js
│   ├── horarioService.js
│   ├── docenteService.js
│   ├── aulaService.js
│   ├── cursoService.js
│   ├── grupoService.js
│   ├── parametroService.js
│   └── asignacionService.js
│
└── websocket/
    └── stompClient.js                # Ver docs/frontend/websocket.md
```

## Componentes UI Clave

### Button

```jsx
export default function Button({ variant = 'primary', size = 'md', loading, children, ...props }) {
  const variants = {
    primary:   'bg-accent-blue hover:bg-blue-700 text-white',
    success:   'bg-accent-green hover:bg-green-700 text-white',
    danger:    'bg-accent-red hover:bg-red-700 text-white',
    secondary: 'bg-bg-tertiary hover:bg-slate-600 text-text-primary',
    ghost:     'hover:bg-bg-tertiary text-text-secondary',
  };
  const sizes = {
    sm: 'px-3 py-1.5 text-sm',
    md: 'px-4 py-2 text-base',
    lg: 'px-6 py-3 text-lg',
  };
  return (
    <button
      className={`rounded-lg font-medium transition disabled:opacity-50 ${variants[variant]} ${sizes[size]}`}
      disabled={loading}
      {...props}
    >
      {loading ? '...' : children}
    </button>
  );
}
```

### Badge

```jsx
const colores = {
  PUBLICADO: 'bg-green-700/30 text-green-300 border border-green-700',
  BORRADOR:  'bg-yellow-700/30 text-yellow-300 border border-yellow-700',
  ACTIVO:    'bg-blue-700/30 text-blue-300 border border-blue-700',
  CERRADO:   'bg-gray-700/30 text-gray-300 border border-gray-700',
  CONFLICTO: 'bg-red-700/30 text-red-300 border border-red-700',
  MANUAL:    'bg-purple-700/30 text-purple-300 border border-purple-700',
  'HC-01': 'bg-red-800/40 text-red-200',  'HC-02': 'bg-red-800/40 text-red-200',
  'HC-03': 'bg-orange-700/40 text-orange-200', 'HC-04': 'bg-orange-700/40 text-orange-200',
  'HC-05': 'bg-yellow-700/40 text-yellow-200', 'HC-06': 'bg-gray-700/40 text-gray-200',
  'HC-07': 'bg-gray-700/40 text-gray-200', 'HC-08': 'bg-gray-700/40 text-gray-200',
  'HC-09': 'bg-blue-700/40 text-blue-200', 'HC-10': 'bg-purple-700/40 text-purple-200',
};

export default function Badge({ children, variant }) {
  return (
    <span className={`px-2 py-0.5 text-xs font-semibold rounded-full ${colores[variant] || colores.BORRADOR}`}>
      {children}
    </span>
  );
}
```

### Sidebar

```jsx
export default function Sidebar() {
  const items = [
    { to: '/admin',            icon: '📊', label: 'Dashboard' },
    { to: '/admin/horario',    icon: '📅', label: 'Horario' },
    { to: '/admin/docentes',   icon: '👥', label: 'Docentes' },
    { to: '/admin/aulas',      icon: '🏫', label: 'Aulas' },
    { to: '/admin/cursos',     icon: '📚', label: 'Cursos' },
    { to: '/admin/grupos',     icon: '👨‍👩‍👧', label: 'Grupos' },
    { to: '/admin/parametros', icon: '⚙️', label: 'Parámetros' },
  ];
  return (
    <aside className="w-64 bg-bg-secondary border-r border-border-color min-h-screen flex flex-col">
      <div className="p-6 border-b border-border-color">
        <h1 className="text-xl font-bold text-text-primary">Horarios UB</h1>
      </div>
      <nav className="flex-1 p-4 space-y-1">
        {items.map(it => (
          <NavLink
            key={it.to}
            to={it.to}
            end={it.to === '/admin'}
            className={({ isActive }) =>
              `flex items-center gap-3 px-3 py-2 rounded-lg transition ${
                isActive ? 'bg-accent-blue text-white' : 'text-text-secondary hover:bg-bg-tertiary'
              }`
            }
          >
            <span>{it.icon}</span>
            <span>{it.label}</span>
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
```

### DataTable (genérica)

```jsx
export default function DataTable({ columns, data, onRowClick, actions }) {
  return (
    <div className="bg-bg-secondary rounded-xl border border-border-color overflow-hidden">
      <table className="w-full">
        <thead className="bg-bg-tertiary">
          <tr>
            {columns.map(col => (
              <th key={col.key} className="px-4 py-3 text-left text-sm font-semibold text-text-secondary">
                {col.label}
              </th>
            ))}
            {actions && <th className="px-4 py-3 text-right text-sm font-semibold">Acciones</th>}
          </tr>
        </thead>
        <tbody>
          {data.map((row, i) => (
            <tr
              key={row.id || i}
              onClick={() => onRowClick?.(row)}
              className="border-t border-border-color hover:bg-bg-tertiary/50 cursor-pointer"
            >
              {columns.map(col => (
                <td key={col.key} className="px-4 py-3 text-text-primary">
                  {col.render ? col.render(row) : row[col.key]}
                </td>
              ))}
              {actions && <td className="px-4 py-3 text-right">{actions(row)}</td>}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
```

## Páginas Principales

### AdminLayout

```jsx
export default function AdminLayout() {
  return (
    <div className="flex min-h-screen bg-bg-primary text-text-primary">
      <Sidebar />
      <div className="flex-1 flex flex-col">
        <TopBar />
        <main className="flex-1 p-6 overflow-auto">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
```

### DocentesPage (patrón CRUD)

```jsx
export default function DocentesPage() {
  const [docentes, setDocentes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState(null);
  const [showForm, setShowForm] = useState(false);

  useEffect(() => {
    docenteService.listar().then(setDocentes).finally(() => setLoading(false));
  }, []);

  const columns = [
    { key: 'nombreCompleto', label: 'Nombre' },
    { key: 'tipoVinculacion', label: 'Vinculación' },
    {
      key: 'compatibilidades',
      label: 'Compatibilidades',
      render: r => `${r.totalCompatibilidades} cursos`,
    },
  ];

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Docentes</h1>
        <Button onClick={() => { setEditing(null); setShowForm(true); }}>
          + Nuevo Docente
        </Button>
      </div>
      {loading ? <LoadingSpinner /> : (
        <DataTable
          columns={columns}
          data={docentes}
          actions={(row) => (
            <>
              <Button size="sm" variant="ghost" onClick={(e) => { e.stopPropagation(); setEditing(row); setShowForm(true); }}>✏️</Button>
              <Button size="sm" variant="ghost" onClick={(e) => { e.stopPropagation(); eliminar(row.docenteId); }}>🗑️</Button>
            </>
          )}
        />
      )}
      {showForm && <DocenteForm docente={editing} onClose={() => setShowForm(false)} onSaved={recargar} />}
    </div>
  );
}
```

### LoginPage

```jsx
export default function LoginPage() {
  const [form, setForm] = useState({ username: '', password: '' });
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const onSubmit = async (e) => {
    e.preventDefault();
    try {
      const { token, rol } = await authService.login(form);
      localStorage.setItem('jwt', token);
      localStorage.setItem('rol', rol);
      navigate(rol === 'ADMIN' ? '/admin' : '/mi-horario');
    } catch (err) {
      setError('Credenciales inválidas');
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-bg-primary">
      <form onSubmit={onSubmit} className="bg-bg-secondary p-8 rounded-xl w-96 space-y-4">
        <h1 className="text-2xl font-bold text-text-primary mb-2">Ingreso al sistema</h1>
        {error && <AlertHC variant="error">{error}</AlertHC>}
        <input
          type="text" placeholder="Usuario"
          value={form.username}
          onChange={e => setForm({ ...form, username: e.target.value })}
          className="w-full px-4 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color"
        />
        <input
          type="password" placeholder="Contraseña"
          value={form.password}
          onChange={e => setForm({ ...form, password: e.target.value })}
          className="w-full px-4 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color"
        />
        <Button type="submit" variant="primary" className="w-full">Entrar</Button>
      </form>
    </div>
  );
}
```

## Servicios HTTP

`src/services/api.js`:

```js
import axios from 'axios';

const api = axios.create({ baseURL: '/api', timeout: 90000 });

api.interceptors.request.use(config => {
  const token = localStorage.getItem('jwt');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  res => res.data,
  err => {
    if (err.response?.status === 401) {
      localStorage.removeItem('jwt');
      window.location.href = '/login';
    }
    return Promise.reject(err);
  }
);

export default api;
```

`src/services/horarioService.js`:

```js
import api from './api';

export const horarioService = {
  generar: (semestre) => api.post('/horarios/generar', { semestre }),
  obtener: (semestre) => api.get(`/horarios/${semestre}`),
  publicar: (id) => api.put(`/horarios/${id}/publicar`),
  conflictos: (semestre) => api.get(`/horarios/${semestre}/conflictos`),
};
```

(Patrón análogo para los demás services).
