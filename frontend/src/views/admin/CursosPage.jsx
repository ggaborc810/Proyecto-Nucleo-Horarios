import { useState, useEffect, useCallback } from 'react';
import Button from '../../components/ui/Button';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import DataTable from '../../components/tables/DataTable';
import AlertHC from '../../components/ui/AlertHC';
import CursoForm from '../../components/forms/CursoForm';
import { cursoService } from '../../services/cursoService';

export default function CursosPage() {
  const [cursos, setCursos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [error, setError] = useState(null);

  const recargar = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await cursoService.listar();
      setCursos(data);
    } catch {
      setError('Error cargando cursos');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { recargar(); }, [recargar]);

  const eliminar = async (id) => {
    if (!confirm('¿Eliminar este curso?')) return;
    try {
      await cursoService.eliminar(id);
      recargar();
    } catch {
      setError('Error eliminando curso');
    }
  };

  const columns = [
    { key: 'codigoCurso', label: 'Código' },
    { key: 'nombreCurso', label: 'Nombre' },
    { key: 'semestreNivel', label: 'Semestre' },
    { key: 'creditos', label: 'Créditos' },
    { key: 'frecuenciaSemanal', label: 'Sesiones/sem' },
    { key: 'nombreTipoAula', label: 'Tipo aula' },
  ];

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Cursos</h1>
        <Button onClick={() => { setEditing(null); setShowForm(true); }}>+ Nuevo Curso</Button>
      </div>
      {error && <AlertHC variant="error" className="mb-4">{error}</AlertHC>}
      {loading ? <LoadingSpinner /> : (
        <DataTable
          columns={columns}
          data={cursos}
          actions={(row) => (
            <div className="flex gap-1 justify-end">
              <Button size="sm" variant="ghost" onClick={(e) => { e.stopPropagation(); setEditing(row); setShowForm(true); }}>✏️</Button>
              <Button size="sm" variant="ghost" onClick={(e) => { e.stopPropagation(); eliminar(row.cursoId); }}>🗑️</Button>
            </div>
          )}
        />
      )}
      {showForm && (
        <CursoForm curso={editing} onClose={() => setShowForm(false)} onSaved={recargar} />
      )}
    </div>
  );
}
