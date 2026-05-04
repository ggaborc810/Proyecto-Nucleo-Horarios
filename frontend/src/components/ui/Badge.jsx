const colores = {
  PUBLICADO: 'bg-green-700/30 text-green-300 border border-green-700',
  BORRADOR:  'bg-yellow-700/30 text-yellow-300 border border-yellow-700',
  ACTIVO:    'bg-blue-700/30 text-blue-300 border border-blue-700',
  CERRADO:   'bg-gray-700/30 text-gray-300 border border-gray-700',
  ASIGNADA:  'bg-blue-700/30 text-blue-300 border border-blue-700',
  CONFLICTO: 'bg-red-700/30 text-red-300 border border-red-700',
  MANUAL:    'bg-purple-700/30 text-purple-300 border border-purple-700',
  'HC-01': 'bg-red-800/40 text-red-200',
  'HC-02': 'bg-red-800/40 text-red-200',
  'HC-03': 'bg-orange-700/40 text-orange-200',
  'HC-04': 'bg-orange-700/40 text-orange-200',
  'HC-05': 'bg-yellow-700/40 text-yellow-200',
  'HC-06': 'bg-gray-700/40 text-gray-200',
  'HC-07': 'bg-gray-700/40 text-gray-200',
  'HC-08': 'bg-gray-700/40 text-gray-200',
  'HC-09': 'bg-blue-700/40 text-blue-200',
  'HC-10': 'bg-purple-700/40 text-purple-200',
};

export default function Badge({ children, variant }) {
  return (
    <span className={`px-2 py-0.5 text-xs font-semibold rounded-full ${colores[variant] || colores.BORRADOR}`}>
      {children}
    </span>
  );
}
