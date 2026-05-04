import { useState, useEffect, useCallback } from 'react';
import Button from '../../components/ui/Button';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import DataTable from '../../components/tables/DataTable';
import Badge from '../../components/ui/Badge';
import AlertHC from '../../components/ui/AlertHC';
import AulaForm from '../../components/forms/AulaForm';
import { aulaService } from '../../services/aulaService';

export default function AulasPage() {
  const [aulas, setAulas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [error, setError] = useState(null);

  const recargar = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await aulaService.listar();
      setAulas(data);
    } catch {
      setError('Error cargando aulas');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { recargar(); }, [recargar]);

  const columns = [
    { key: 'codigoAula', label: 'Código' },
    { key: 'nombreAula', label: 'Nombre' },
    { key: 'capacidad', label: 'Capacidad' },
    { key: 'nombreTipoAula', label: 'Tipo' },
    {
      key: 'activa',
      label: 'Estado',
      render: (row) => (
        <Badge variant={row.activa ? 'ACTIVO' : 'CERRADO'}>
          {row.activa ? 'Activa' : 'Inactiva'}
        </Badge>
      ),
    },
  ];

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Aulas</h1>
        <Button onClick={() => { setEditing(null); setShowForm(true); }}>+ Nueva Aula</Button>
      </div>
      {error && <AlertHC variant="error" className="mb-4">{error}</AlertHC>}
      {loading ? <LoadingSpinner /> : (
        <DataTable
          columns={columns}
          data={aulas}
          actions={(row) => (
            <div className="flex gap-1 justify-end">
              <Button size="sm" variant="ghost" onClick={(e) => { e.stopPropagation(); setEditing(row); setShowForm(true); }}>✏️</Button>
            </div>
          )}
        />
      )}
      {showForm && (
        <AulaForm aula={editing} onClose={() => setShowForm(false)} onSaved={recargar} />
      )}
    </div>
  );
}
