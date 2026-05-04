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

import DocenteLayout from './views/docente/DocenteLayout';
import MiHorarioPage from './views/docente/MiHorarioPage';
import MisGruposPage from './views/docente/MisGruposPage';
import DisponibilidadPage from './views/docente/DisponibilidadPage';

import PublicoLayout from './views/publico/PublicoLayout';
import HorarioPublicoPage from './views/publico/HorarioPublicoPage';
import GruposPublicoPage from './views/publico/GruposPublicoPage';
import GrupoDetallePage from './views/publico/GrupoDetallePage';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Raíz → login */}
        <Route path="/" element={<Navigate to="/login" replace />} />

        {/* Público — sin auth */}
        <Route element={<PublicoLayout />}>
          <Route path="/horario/:semestre" element={<HorarioPublicoPage />} />
          <Route path="/grupos" element={<GruposPublicoPage />} />
          <Route path="/grupos/:id" element={<GrupoDetallePage />} />
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
          <Route path="/docente" element={<DocenteLayout />}>
            <Route index element={<Navigate to="/docente/mi-horario" replace />} />
            <Route path="mi-horario" element={<MiHorarioPage />} />
            <Route path="mis-grupos" element={<MisGruposPage />} />
            <Route path="disponibilidad" element={<DisponibilidadPage />} />
          </Route>
        </Route>

        {/* Estudiante — redirige a la vista pública */}
        <Route path="/estudiante" element={<Navigate to="/horario/2026-1" replace />} />

        {/* Ruta legacy de docente */}
        <Route path="/mi-horario" element={<Navigate to="/docente/mi-horario" replace />} />

        {/* Catch-all */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
