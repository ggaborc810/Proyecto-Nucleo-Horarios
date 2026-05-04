/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        'bg-primary':     'var(--bg-primary)',
        'bg-secondary':   'var(--bg-secondary)',
        'bg-tertiary':    'var(--bg-tertiary)',
        'border-color':   'var(--border-color)',
        'accent-blue':    'var(--accent-blue)',
        'accent-green':   'var(--accent-green)',
        'accent-red':     'var(--accent-red)',
        'accent-yellow':  'var(--accent-yellow)',
        'accent-purple':  'var(--accent-purple)',
        'text-primary':   'var(--text-primary)',
        'text-secondary': 'var(--text-secondary)',
        'text-muted':     'var(--text-muted)',
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      },
    },
  },
  plugins: [],
}

