# Plan de Implementación

## Orden Recomendado por Fases

### Fase 1 — Cimientos (BD + Proyecto)

1. Crear proyecto Spring Boot con dependencias (`docs/backend/config.md`)
2. Crear proyecto React con Vite (`docs/frontend/design-system.md`)
3. Ejecutar DDL completo en PostgreSQL (`docs/database.md`)
4. Insertar datos iniciales mínimos
5. Verificar conexión backend ↔ BD

**Verificación**: `mvn spring-boot:run` levanta sin errores. La aplicación se conecta a PostgreSQL.

---

### Fase 2 — Backend Core (sin motor)

1. Implementar las 12 entidades JPA → ver `docs/backend/entities.md`
2. Implementar los repositorios → ver `docs/backend/repositories.md`
3. Implementar JWT + Spring Security → ver `docs/backend/security.md`
4. Implementar servicios CRUD básicos → ver `docs/backend/services.md`
5. Implementar controladores REST → ver `docs/backend/controllers.md`

**Verificación**:
- `POST /api/auth/login` retorna JWT válido
- `GET /api/publico/horario/2026-1` responde 200 sin token
- `GET /api/docentes` sin token responde 401
- CRUD de docentes funciona con Postman/curl

---

### Fase 3 — Motor Greedy (CRÍTICA)

Esta es la fase más importante. **Tests primero, código después.**

1. Crear value objects de dominio (sin Spring/JPA): `AsignacionCandidato`, `HorarioContexto`, `ResultadoGeneracion`, `ConflictoAsignacion`
2. Definir interfaces `HCEvaluator` y `SoftEvaluator` → ver `docs/algorithm/hc-evaluators.md`
3. Para CADA HC (de HC-01 a HC-10):
   - Escribir test unitario (red)
   - Implementar evaluador (green)
   - Refactorizar (refactor)
4. Implementar SoftEvaluators (SC-01, SC-02) → ver `docs/algorithm/soft-evaluators.md`
5. Implementar `SchedulerEngine` → ver `docs/algorithm/pseudocode.md`
6. Tests del motor con casos completos
7. Integrar motor en `HorarioService.generarHorario()`

**Verificación**:
- Suite del motor corre en < 10 segundos sin `@SpringBootTest`
- `POST /api/horarios/generar` funciona con 5 grupos de prueba
- El reporte de conflictos identifica el HC violado correctamente

---

### Fase 4 — WebSocket (Drag-and-Drop Backend)

1. Configurar STOMP en Spring (`docs/backend/controllers.md` sección WebSocket)
2. Implementar `AsignacionWebSocketController`
3. Implementar `AsignacionService.validarMovimiento()`

**Verificación**: cliente STOMP de prueba puede enviar `MovimientoDTO` y recibir `ValidacionMovimientoDTO`.

---

### Fase 5 — Frontend

Orden de implementación dentro del frontend:

1. **Infraestructura**:
   - `services/api.js` (axios + interceptores JWT)
   - `websocket/stompClient.js`
   - `hooks/useAuth.js`
   - Router principal en `App.jsx`

2. **UI base**: Button, Badge, Modal, Sidebar, TopBar, DataTable, LoadingSpinner

3. **Auth**: LoginPage, PrivateRoute

4. **CRUD datos maestros** (en orden):
   - DocentesPage (+ disponibilidades + compatibilidades)
   - AulasPage
   - CursosPage
   - GruposPage
   - ParametrosPage

5. **Calendario (prioridad alta)** — ver `docs/frontend/calendar.md`:
   - CeldaCalendario (drop target)
   - BloqueAsignacion (draggable)
   - CalendarioSemanal (grid)
   - ConflictoPanel
   - useCalendario, useWebSocket
   - CalendarioPage

6. **Generación**: HorarioGenerarPage + ConflictosTable

7. **Vista pública**: FilterBar + HorarioPublicoPage

8. **Vista docente**: MiHorarioPage

---

### Fase 6 — Pruebas y Aceptación

1. Tests de integración con `@SpringBootTest` → ver `specs/tests.md`
2. Prueba de rendimiento: 30 grupos en < 60s
3. Pruebas de seguridad: rutas públicas vs admin
4. Checklist final de aceptación → `specs/tests.md` sección final

---

## Señales de Advertencia 🚨

Si encuentras algo de esto, **detente y corrige antes de continuar**:

- Strings de hora hardcodeadas (`"07:00"`, `"22:00"`, `"12:00"`) en lógica de negocio → mover a `ParametroSemestre`
- `SchedulerEngine` importa `javax.persistence` o `org.springframework.data` → extraer a interfaz de repositorio
- Test del motor requiere `@SpringBootTest` → refactorizar a Mockito puro
- React tiene lógica HC duplicada → mover al backend vía WebSocket
- Motor hace queries SQL durante el ciclo de asignación → mover a carga inicial

---

## Comandos Frecuentes

```bash
# Backend
cd backend
mvn clean test                    # Solo tests
mvn spring-boot:run              # Servidor dev
mvn test -Dtest="*EvaluatorTest" # Solo tests del motor

# Frontend
cd frontend
npm install
npm run dev                       # Dev server (http://localhost:5173)
npm run build                     # Build producción

# Variables de entorno (backend)
export DB_URL=jdbc:postgresql://localhost:5432/horarios_db
export DB_USER=postgres
export DB_PASSWORD=tu_password
export JWT_SECRET=clave-de-32-caracteres-mínimo
```
