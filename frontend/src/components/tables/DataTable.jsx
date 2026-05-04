import LoadingSpinner from '../ui/LoadingSpinner';

export default function DataTable({ columns, data, loading, actions, emptyText = 'Sin registros' }) {
  if (loading) return <LoadingSpinner />;

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
            {actions && (
              <th className="px-4 py-3 text-right text-sm font-semibold text-text-secondary">Acciones</th>
            )}
          </tr>
        </thead>
        <tbody>
          {data.length === 0 ? (
            <tr>
              <td colSpan={columns.length + (actions ? 1 : 0)} className="px-4 py-8 text-center text-text-muted">
                {emptyText}
              </td>
            </tr>
          ) : data.map((row, i) => (
            <tr key={row.id || i} className="border-t border-border-color hover:bg-bg-tertiary/40 transition">
              {columns.map(col => (
                <td key={col.key} className="px-4 py-3 text-text-primary text-sm">
                  {col.render ? col.render(row) : row[col.key]}
                </td>
              ))}
              {actions && (
                <td className="px-4 py-3 text-right">{actions(row)}</td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
