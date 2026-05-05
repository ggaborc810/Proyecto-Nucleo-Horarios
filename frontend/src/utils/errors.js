export function getErrorMessage(error, fallback = 'No se pudo completar la acción.') {
  const data = error?.response?.data;
  const message = data?.mensaje || data?.message;

  if (typeof message === 'string' && message.trim()) {
    return message.trim();
  }

  if (Array.isArray(data?.detalles) && data.detalles.length > 0) {
    return data.detalles.filter(Boolean).join(' ');
  }

  return fallback;
}
