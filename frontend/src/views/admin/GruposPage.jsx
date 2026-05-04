import { useState, useEffect, useCallback } from 'react';
import Button from '../../components/ui/Button';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import DataTable from '../../components/tables/DataTable';
import Badge from '../../components/ui/Badge';
import AlertHC from '../../components/ui/AlertHC';
import GrupoForm from '../../components/forms/GrupoForm';
import { grupoService } from '../../services/grupoService';

export default function GruposPage() {
  const [grupos, setGrupos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [filtroEstado, setFiltroEstado] = useState('');
  const [error, setError] = useState(null);

  const recargar = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await grupoService.listar(filtroEstado || undefined);
      setGrupos(data);
    } catch {
      setError('Error cargando grupos');
    } finally {
      setLoading(false);
    }
  }, [filtroEstado]);

  useEffect(() => { recargar(); }, [recargar]);

  const cerrarGrupo = async (id) => {
    if (!confirm('¿Cerrar este grupo? No se podrán agregar más inscritos.')) return;
    try {
      await grupoService.cerrar(id);
      recargar();
    } catch {
      setError('Error cerrando grupo');
    }
  };

  const reabrirGrupo = async (id) => {
    if (!confirm('¿Reabrir este grupo? Volverá a estado ACTIVO.')) return;
    try {
      await grupoService.reabrir(id);
      recargar();
    } catch {
      setError('Error reabriendo grupo');
    }
  };

  const columns = [
    { key: 'seccion', label: 'Sección' },
    { key: 'nombreCurso', label: 'Curso' },
    { key: 'nombreDocente', label: 'Docente' },
    { key: 'numInscritos', label: 'Inscritos' },
    {
      key: 'estado',
      label: 'Estado',
      render: (row) => <Badge variant={row.estado}>{row.estado}</Badge>,
    },
  ];

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Grupos</h1>
        <select
          value={filtroEstado}
          onChange={e => setFiltroEstado(e.target.value)}
          className="px-3 py-2 bg-bg-tertiary text-text-primary rounded-lg border border-border-color text-sm"
        >
          <option value="">Todos los estados</option>
          <option value="ACTIVO">Activo</option>
          <option value="CERRADO">Cerrado</option>
        </select>
      </div>
      {error && <AlertHC variant="error" className="mb-4">{error}</AlertHC>}
      {loading ? <LoadingSpinner /> : (
        <DataTable
          columns={columns}
          data={grupos}
          actions={(row) => (
            <div className="flex gap-1 justify-end">
              <Button size="sm" variant="ghost" onClick={(e) => { e.stopPropagation(); setEditing(row); setShowForm(true); }}>✏️</Button>
              {row.estado === 'ACTIVO' && (
                <Button size="sm" variant="danger" onClick={(e) => { e.stopPropagation(); cerrarGrupo(row.grupoId); }}>
                  Cerrar
                </Button>
              )}
              {row.estado === 'CERRADO' && (
                <Button size="sm" variant="secondary" onClick={(e) => { e.stopPropagation(); reabrirGrupo(row.grupoId); }}>
                  Reabrir
                </Button>
              )}
            </div>
          )}
        />
      )}
      {showForm && (
        <GrupoForm grupo={editing} onClose={() => setShowForm(false)} onSaved={recargar} />
      )}
    </div>
  );
}
