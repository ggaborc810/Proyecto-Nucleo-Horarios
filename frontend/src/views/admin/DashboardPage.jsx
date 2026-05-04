import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Button from '../../components/ui/Button';
import Badge from '../../components/ui/Badge';
import { horarioService } from '../../services/horarioService';
import { docenteService } from '../../services/docenteService';
import { grupoService } from '../../services/grupoService';

export default function DashboardPage() {
  const [stats, setStats] = useState(null);
  const [horario, setHorario] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    Promise.allSettled([
      docenteService.listar(),
      grupoService.listar('ACTIVO'),
      horarioService.obtener('2026-1'),
    ]).then(([docs, grupos, hor]) => {
      setStats({
        docentes: docs.status === 'fulfilled' ? docs.value.length : 0,
        grupos: grupos.status === 'fulfilled' ? grupos.value.length : 0,
      });
      if (hor.status === 'fulfilled') setHorario(hor.value);
    });
  }, []);

  const totalAsig = horario?.asignaciones?.filter(a => a.estado === 'ASIGNADA').length || 0;
  const totalConf = horario?.asignaciones?.filter(a => a.estado === 'CONFLICTO').length || 0;

  return (
    <div>
      <h1 className="text-2xl font-bold text-text-primary mb-6">Dashboard</h1>
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
        {[
          { label: 'Docentes', value: stats?.docentes ?? '—', color: 'text-accent-blue' },
          { label: 'Grupos activos', value: stats?.grupos ?? '—', color: 'text-accent-green' },
          { label: 'Asignaciones', value: totalAsig, color: 'text-accent-blue' },
          { label: 'Conflictos', value: totalConf, color: totalConf > 0 ? 'text-accent-red' : 'text-accent-green' },
        ].map(card => (
          <div key={card.label} className="bg-bg-secondary border border-border-color rounded-xl p-5">
            <p className="text-text-secondary text-sm">{card.label}</p>
            <p className={`text-3xl font-bold mt-1 ${card.color}`}>{card.value}</p>
          </div>
        ))}
      </div>

      <div className="bg-bg-secondary border border-border-color rounded-xl p-5">
        <div className="flex items-center justify-between mb-4">
          <div>
            <h2 className="text-lg font-semibold text-text-primary">Semestre 2026-1</h2>
            {horario && <Badge variant={horario.estado}>{horario.estado}</Badge>}
          </div>
          <div className="flex gap-3">
            <Button variant="secondary" onClick={() => navigate('/admin/horario/generar')}>
              Generar horario
            </Button>
            <Button variant="primary" onClick={() => navigate('/admin/horario')}>
              Ver calendario
            </Button>
          </div>
        </div>
        {!horario && (
          <p className="text-text-muted text-sm">No hay horario generado para 2026-1.</p>
        )}
      </div>
    </div>
  );
}
