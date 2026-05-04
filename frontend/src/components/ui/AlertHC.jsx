const styles = {
  error:   'bg-red-900/30 border-red-700 text-red-300',
  warning: 'bg-yellow-900/30 border-yellow-700 text-yellow-300',
  success: 'bg-green-900/30 border-green-700 text-green-300',
  info:    'bg-blue-900/30 border-blue-700 text-blue-300',
};

export default function AlertHC({ variant = 'error', children }) {
  const icons = { error: '✕', warning: '⚠', success: '✓', info: 'ℹ' };
  return (
    <div className={`flex items-start gap-3 px-4 py-3 rounded-lg border text-sm ${styles[variant]}`}>
      <span className="font-bold">{icons[variant]}</span>
      <span>{children}</span>
    </div>
  );
}
