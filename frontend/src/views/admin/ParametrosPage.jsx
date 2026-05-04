import { useState, useEffect, useCallback } from 'react';
import Button from '../../components/ui/Button';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import DataTable from '../../components/tables/DataTable';
import Badge from '../../components/ui/Badge';
import AlertHC from '../../components/ui/AlertHC';
import ParametroSemestreForm from '../../components/forms/ParametroSemestreForm';
import { parametroService } from '../../services/parametroService';

export default function ParametrosPage() {
  const [parametros, setParametros] = useState([]);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [error, setError] = useState(null);

  const recargar = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await parametroService.listar();
      setParametros(Array.isArray(data) ? data : [data]);
    } catch {
      setError('Error cargando parámetros');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { recargar(); }, [recargar]);

  const columns = [
    { key: 'semestre', label: 'Semestre' },
    { key: 'fechaInicioSemestre', label: 'Fecha inicio' },
    { key: 'fechaFinSemestre', label: 'Fecha fin' },
    {
      key: 'horaInicioJornada',
      label: 'Jornada',
      render: (row) => `${row.horaInicioJornada?.slice(0, 5)} – ${row.horaFinJornada?.slice(0, 5)}`,
    },
    {
      key: 'duracionSesionMinutos',
      label: 'Sesión',
      render: (row) => `${row.duracionSesionMinutos} min`,
    },
    {
      key: 'franjas',
      label: 'Franjas',
      render: (row) => `${row.totalFranjas ?? 0}`,
    },
  ];

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Parámetros de Semestre</h1>
        <Button onClick={() => { setEditing(null); setShowForm(true); }}>+ Nuevo Semestre</Button>
      </div>
      {error && <AlertHC variant="error" className="mb-4">{error}</AlertHC>}
      {loading ? <LoadingSpinner /> : (
        <DataTable
          columns={columns}
          data={parametros}
          actions={(row) => (
            <div className="flex gap-1 justify-end">
              <Button size="sm" variant="ghost" onClick={(e) => { e.stopPropagation(); setEditing(row); setShowForm(true); }}>✏️</Button>
            </div>
          )}
        />
      )}
      {showForm && (
        <ParametroSemestreForm parametro={editing} onClose={() => setShowForm(false)} onSaved={recargar} />
      )}
    </div>
  );
}
