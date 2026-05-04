export default function Button({ variant = 'primary', size = 'md', loading, className = '', children, ...props }) {
  const variants = {
    primary:   'bg-accent-blue hover:bg-blue-700 text-white',
    success:   'bg-accent-green hover:bg-green-700 text-white',
    danger:    'bg-accent-red hover:bg-red-700 text-white',
    secondary: 'bg-bg-tertiary hover:bg-slate-600 text-text-primary',
    ghost:     'hover:bg-bg-tertiary text-text-secondary',
  };
  const sizes = {
    sm: 'px-3 py-1.5 text-sm',
    md: 'px-4 py-2 text-base',
    lg: 'px-6 py-3 text-lg',
  };
  return (
    <button
      className={`rounded-lg font-medium transition disabled:opacity-50 cursor-pointer ${variants[variant]} ${sizes[size]} ${className}`}
      disabled={loading || props.disabled}
      {...props}
    >
      {loading ? (
        <span className="flex items-center gap-2">
          <span className="animate-spin w-4 h-4 border-2 border-current border-t-transparent rounded-full inline-block" />
          Cargando...
        </span>
      ) : children}
    </button>
  );
}
