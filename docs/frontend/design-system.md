# Sistema de Diseño — Frontend

## Estilo Visual

Panel de administración: **dark mode** moderno y profesional. Vista pública: claro y accesible.

## Variables CSS Globales

Definir en `src/index.css` y referenciar desde Tailwind config y componentes:

```css
:root {
  /* Dark UI — Panel de Administración */
  --bg-primary:    #0F172A;   /* Fondo principal */
  --bg-secondary:  #1E293B;   /* Cards, paneles */
  --bg-tertiary:   #334155;   /* Hover states, inputs */
  --border-color:  #475569;
  
  /* Acentos */
  --accent-blue:        #3B82F6;
  --accent-blue-hover:  #2563EB;
  --accent-green:       #10B981;
  --accent-red:         #EF4444;
  --accent-yellow:      #F59E0B;
  --accent-purple:      #8B5CF6;
  
  /* Texto */
  --text-primary:    #F1F5F9;
  --text-secondary:  #94A3B8;
  --text-muted:      #64748B;
  
  /* Vista pública (claro) */
  --pub-bg:      #F8FAFC;
  --pub-card:    #FFFFFF;
  --pub-border:  #E2E8F0;
  --pub-text:    #1E293B;
  
  /* Calendario */
  --cal-header:               #1E293B;
  --cal-cell-empty:           #0F172A;
  --cal-cell-assigned:        #1D4ED8;
  --cal-cell-conflict:        #7F1D1D;
  --cal-cell-dragging:        #5B21B6;
  --cal-cell-hover-valid:     #065F46;
  --cal-cell-hover-invalid:   #7F1D1D;
}
```

## Tipografía

```css
font-family: 'Inter', 'SF Pro Display', system-ui, sans-serif;

/* Escalas */
--font-size-xs:   0.75rem;    /* 12px — labels */
--font-size-sm:   0.875rem;   /* 14px — texto tabla */
--font-size-base: 1rem;       /* 16px — texto normal */
--font-size-lg:   1.125rem;   /* 18px — subtítulos */
--font-size-xl:   1.25rem;    /* 20px — títulos sección */
--font-size-2xl:  1.5rem;     /* 24px — títulos página */
--font-size-3xl:  1.875rem;   /* 30px — header principal */
```

Fuente recomendada: importar Inter desde Google Fonts en `index.html`.

## Espaciado

Usar la escala estándar de Tailwind (4px = 1 unit). Reglas:

- Gap entre cards: `gap-4` (16px) o `gap-6` (24px)
- Padding interno de card: `p-4` (16px) o `p-6` (24px)
- Margen entre secciones: `mb-8` (32px)

## Stack Frontend

- **Build**: Vite 5
- **Framework**: React 18
- **Estilos**: Tailwind CSS 3
- **Routing**: React Router 6
- **HTTP**: Axios
- **WebSocket**: `@stomp/stompjs` + `sockjs-client`
- **Drag-and-Drop**: `@dnd-kit/core`
- **Fechas**: `date-fns`

## `package.json` Dependencias

```json
{
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.22.0",
    "axios": "^1.6.7",
    "@stomp/stompjs": "^7.0.0",
    "sockjs-client": "^1.6.1",
    "@dnd-kit/core": "^6.1.0",
    "@dnd-kit/sortable": "^8.0.0",
    "@dnd-kit/utilities": "^3.2.2",
    "date-fns": "^3.3.1"
  },
  "devDependencies": {
    "@vitejs/plugin-react": "^4.2.1",
    "vite": "^5.1.0",
    "tailwindcss": "^3.4.0",
    "autoprefixer": "^10.4.17",
    "postcss": "^8.4.33"
  }
}
```

## Tailwind Config

`tailwind.config.js`:

```js
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        'bg-primary':   'var(--bg-primary)',
        'bg-secondary': 'var(--bg-secondary)',
        'bg-tertiary':  'var(--bg-tertiary)',
        'border-color': 'var(--border-color)',
        'accent-blue':  'var(--accent-blue)',
        'accent-green': 'var(--accent-green)',
        'accent-red':   'var(--accent-red)',
        'accent-yellow':'var(--accent-yellow)',
        'accent-purple':'var(--accent-purple)',
        'text-primary':   'var(--text-primary)',
        'text-secondary': 'var(--text-secondary)',
        'text-muted':     'var(--text-muted)',
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      },
    },
  },
};
```

## Inicialización del Proyecto

```bash
npm create vite@latest frontend -- --template react
cd frontend
npm install
npm install axios @stomp/stompjs sockjs-client @dnd-kit/core @dnd-kit/sortable @dnd-kit/utilities date-fns react-router-dom
npm install -D tailwindcss autoprefixer postcss
npx tailwindcss init -p
```

## Configuración de Vite (proxy al backend)

`vite.config.js`:

```js
export default {
  plugins: [react()],
  server: {
    proxy: {
      '/api': 'http://localhost:8080',
      '/ws':  { target: 'http://localhost:8080', ws: true },
    },
  },
};
```
