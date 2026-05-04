export default function LoadingSpinner({ text = 'Cargando...' }) {
  return (
    <div className="flex flex-col items-center justify-center py-16 gap-3">
      <div className="animate-spin w-10 h-10 border-4 border-accent-blue border-t-transparent rounded-full" />
      <p className="text-text-secondary text-sm">{text}</p>
    </div>
  );
}
