import { useState, useEffect, useCallback } from 'react';
import Button from '../../components/ui/Button';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import DataTable from '../../components/tables/DataTable';
import AlertHC from '../../components/ui/AlertHC';
import ParametroSemestreForm from '../../components/forms/ParametroSemestreForm';
import { parametroService } from '../../services/parametroService';
import { getErrorMessage } from '../../utils/errors';

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
    } catch (err) {
      setError(getErrorMessage(err, 'No se pudieron cargar los parámetros.'));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { recargar(); }, [recargar]);

  const columns = [
    { key: 'semestre', label: 'Semestre' },
    {
      key: 'jornadaLV',
      label: 'Lun-vie',
      render: (row) => `${row.franjaInicioLV?.slice(0, 5)} - ${row.franjaFinLV?.slice(0, 5)}`,
    },
    {
      key: 'jornadaSA',
      label: 'Sábado',
      render: (row) => `${row.franjaInicioSA?.slice(0, 5)} - ${row.franjaFinSA?.slice(0, 5)}`,
    },
    {
      key: 'exclusion',
      label: 'Exclusión',
      render: (row) => `${row.exclusionInicio?.slice(0, 5)} - ${row.exclusionFin?.slice(0, 5)}`,
    },
    {
      key: 'capMaxGrupo',
      label: 'Cap. grupo',
    },
    {
      key: 'activo',
      label: 'Activo',
      render: (row) => row.activo ? 'Sí' : 'No',
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
              <Button size="sm" variant="ghost" onClick={(e) => { e.stopPropagation(); setEditing(row); setShowForm(true); }}>Editar</Button>
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
