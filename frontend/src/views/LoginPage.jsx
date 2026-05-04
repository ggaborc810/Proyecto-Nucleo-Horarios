import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Button from '../components/ui/Button';
import AlertHC from '../components/ui/AlertHC';
import { authService } from '../services/authService';

export default function LoginPage() {
  const [form, setForm] = useState({ username: '', password: '' });
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const onChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const onSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      const { token, rol, username, docenteId } = await authService.login(form);
      localStorage.setItem('jwt', token);
      localStorage.setItem('rol', rol);
      localStorage.setItem('username', username);
      if (docenteId != null) localStorage.setItem('docenteId', String(docenteId));
      else localStorage.removeItem('docenteId');
      if (rol === 'ADMIN') navigate('/admin', { replace: true });
      else if (rol === 'DOCENTE') navigate('/docente', { replace: true });
      else if (rol === 'ESTUDIANTE') navigate('/estudiante', { replace: true });
      else navigate('/', { replace: true });
    } catch {
      setError('Usuario o contraseña incorrectos');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-bg-primary">
      <div className="w-full max-w-sm">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-text-primary">Horarios UEB</h1>
          <p className="text-text-secondary mt-1 text-sm">Universidad El Bosque · Ing. Sistemas</p>
        </div>
        <form onSubmit={onSubmit} className="bg-bg-secondary border border-border-color rounded-xl p-8 space-y-4 shadow-xl">
          <h2 className="text-xl font-semibold text-text-primary">Iniciar sesión</h2>
          {error && <AlertHC variant="error">{error}</AlertHC>}
          <div>
            <label className="block text-sm text-text-secondary mb-1">Usuario</label>
            <input
              name="username" value={form.username} onChange={onChange} required autoFocus
              className="w-full px-4 py-2.5 bg-bg-tertiary text-text-primary rounded-lg border border-border-color focus:outline-none focus:border-accent-blue transition"
              placeholder="admin"
            />
          </div>
          <div>
            <label className="block text-sm text-text-secondary mb-1">Contraseña</label>
            <input
              name="password" type="password" value={form.password} onChange={onChange} required
              className="w-full px-4 py-2.5 bg-bg-tertiary text-text-primary rounded-lg border border-border-color focus:outline-none focus:border-accent-blue transition"
              placeholder="••••••••"
            />
          </div>
          <Button type="submit" variant="primary" loading={loading} className="w-full justify-center">
            Entrar
          </Button>
        </form>
        <p className="text-center text-text-muted text-xs mt-4">
          <a href="/horario/2026-1" className="hover:text-accent-blue transition">Ver horario público →</a>
        </p>
      </div>
    </div>
  );
}
