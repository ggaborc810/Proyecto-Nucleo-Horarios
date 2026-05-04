import { Outlet } from 'react-router-dom';
import Sidebar from '../../components/ui/Sidebar';
import TopBar from '../../components/ui/TopBar';

export default function AdminLayout() {
  return (
    <div className="flex min-h-screen bg-bg-primary text-text-primary">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0">
        <TopBar homeTo="/admin" roleLabel="Admin" actionLink={{ to: '/horario/2026-1', label: 'Vista publica' }} />
        <main className="flex-1 p-6 overflow-auto">
          <div className="max-w-7xl w-full mx-auto">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  );
}
