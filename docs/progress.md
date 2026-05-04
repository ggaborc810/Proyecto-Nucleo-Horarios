# progress.md — Estado del Proyecto

> **Instrucción para Claude Code**: Lee este archivo al inicio de cada sesión para saber en qué punto estamos.
> Actualízalo al terminar cada paso: cambia ⬜ a ✅ cuando esté completo, ❌ si falló, 🔄 si está en progreso.
> Al finalizar la sesión, reescribe la sección "Próximo Paso" con exactitud.

---

## Estado General

| Fase | Nombre | Estado |
|------|--------|--------|
| Fase 1 | Cimientos (BD + Proyecto) | ✅ Completa |
| Fase 2 | Backend Core (sin motor) | ✅ Completa |
| Fase 3 | Motor Greedy | ✅ Completa (incluyendo correcciones post-sesión 2026-05-02) |
| Fase 4 | WebSocket Drag-and-Drop | ✅ Completa |
| Fase 5 | Frontend React | ✅ Completa (build verificado, correcciones aplicadas) |
| Fase 6 | Pruebas y Aceptación | 🔄 En progreso (revision documental y verificacion basica 2026-05-04) |

---

## Revision 2026-05-04

- 🔄 Se creo `docs/estado-aceptacion.md` con el estado real contrastado contra codigo y especificaciones.
- 🔄 Se actualizaron referencias documentales a React 19, HC-01 a HC-13 y SC-01 a SC-04.
- ✅ Verificacion ejecutada: backend `mvnw test` OK (52 tests), frontend `npm run build` OK, backend dev OK, frontend dev OK.
- ✅ Prueba API basica: login admin OK, `/api/auth/me` OK, `/api/docentes` sin token 401, `/api/docentes` con admin 200, vista publica 200.
- ⚠ No se ejecuto `POST /api/horarios/generar` porque borra asignaciones previas del semestre antes de regenerar.
- ⬜ Siguen pendientes los tests automatizados de integracion, seguridad, rendimiento y aceptacion descritos en `specs/tests.md`.
- ✅ No se hicieron cambios de logica ni se agregaron funcionalidades durante esta revision.

---

## Fase 1 — Cimientos

### Pasos

- ✅ 1.1 Proyecto Spring Boot creado (`backend/`)
- ✅ 1.2 Proyecto React + Vite creado (`frontend/`)
- ✅ 1.3 Base de datos `horarios_db` creada en PostgreSQL
- ✅ 1.4 DDL ejecutado — 12 tablas creadas en orden correcto
- ✅ 1.5 Backend levanta y conecta a BD sin errores (`mvn spring-boot:run`)
- ✅ 1.6 Frontend levanta sin errores (`npm run dev`)

### Verificaciones
- ✅ `\dt` en psql muestra las tablas
- ✅ Backend responde en `http://localhost:8080`
- ✅ Frontend responde en `http://localhost:5173`

### Notas
```
- Java 23.0.2 (JDK instalado), compilación targeting --release 17 en pom.xml
- Maven 3.9.6 instalado en C:\apache-maven-3.9.6 (no en PATH global)
  → Para usarlo: $env:PATH = "C:\apache-maven-3.9.6\bin;$env:PATH"
- PostgreSQL 18, servicio postgresql-x64-18
  → psql: C:\Program Files\PostgreSQL\18\bin\psql.exe
  → Usuario: postgres, Contraseña: postgres
- Base de datos: horarios_db
- seed.sql ejecutado: 25 docentes, 25 aulas, 81 cursos, 99 compatibilidades, 48 grupos,
  38 franjas horarias, 1 horario PUBLICADO (2026-1), 26 usuarios (admin123)
```

---

## Fase 2 — Backend Core

### Pasos

- ✅ 2.1–2.15 Todas las entidades, repositorios, servicios, controladores, DTOs, seguridad JWT implementados

### Verificaciones
- ✅ `mvn clean compile` sin errores
- ✅ `POST /api/auth/login` retorna token JWT (admin/admin123)
- ✅ `GET /api/publico/horario/2026-1` retorna 200 sin token
- ✅ `GET /api/docentes` sin token retorna 401

---

## Fase 3 — Motor Greedy

### Pasos

- ✅ 3.1–3.8 Implementación completa original (ver sesiones anteriores)

### Correcciones aplicadas en sesión 2026-05-02

- ✅ **Bug crítico: `break` prematuro en ciclo principal** — cuando una sesión no tenía candidatos,
  `break` abandonaba todas las sesiones restantes del grupo. Cambiado a `asignadas++; continue;`
  → ahora un grupo con frecuencia=3 obtiene las 3 sesiones aunque alguna genere conflicto.

- ✅ **Bug igual en repair pass** — `break` en `repararConflictos` cambiado a `asignadas++`
  para continuar intentando otras sesiones.

- ✅ **Distribución por días** en `seleccionarMejor()` — pre-filtrado de candidatos en días
  no usados por el grupo antes de evaluar SCs.

- ✅ **HC-11 — Ventana horaria por curso** — `FranjaEnRangoCursoEvaluator`:
  verifica `franja.horaInicio >= curso.horaInicioPerm` y `franja.horaValida <= curso.horaFinPerm`.
  Requirió:
  - `Curso.java`: nuevos campos `horaInicioPerm` (default 07:00) y `horaFinPerm` (default 22:00)
  - `CursoDTO.java`: campos añadidos al record y al método `from()`
  - `CursoService.java`: `crear()` y `actualizar()` persisten los nuevos campos
  - `init.sql`: columnas `hora_inicio_permitida`, `hora_fin_permitida` añadidas a CREATE TABLE
  - BD live: `ALTER TABLE curso ADD COLUMN hora_inicio_permitida ... hora_fin_permitida ...`

- ✅ **HC-12 — Sin traslapes de semestre** — `SinTraslapesSemestreEvaluator`:
  impide que dos grupos del mismo `semestreNivel` compartan la misma franja `(día+hora)`.
  Sin este HC, alumnos de semestre 1 tendrían dos clases al mismo tiempo.

- ✅ **HC-13 — Bloques horarios únicos por grupo** — `SinRepeticionBloqueGrupoEvaluator`:
  impide que las sesiones semanales de un grupo usen la misma `horaInicio` en días distintos.
  Ejemplo inválido: Lunes 07-09 + Martes 07-09. Ejemplo válido: Lunes 07-09 + Miércoles 13-15.

- ✅ **SC-03 — Distribución global de franjas** — `DistribucionFranjasSoftEvaluator`:
  penaliza franjas que ya tienen muchos grupos asignados a nivel global (-20 por uso).
  Resultado: las sesiones se distribuyen entre 07:00, 09:00, 13:00, 15:00, etc.

- ✅ **SC-04 — Distribución por semestre y día** — `DistribucionSemestreDiaSoftEvaluator`:
  penaliza asignar al mismo día cuando ese semestre ya tiene varias sesiones ese día (-20 por sesión).
  Evita que semestre 1 tenga 4 materias el lunes y nada el jueves.

- ✅ **`HorarioContexto`** ampliado con:
  - `semestreFranjaOcupada` (Set) → HC-12
  - `horasInicioGrupo` (Map<grupoId, Set<LocalTime>>) → HC-13
  - `sesionesPorFranja` (Map<franjaId, count>) → SC-03
  - `sesionesPorSemestreDia` (Map<"semestre_dia", count>) → SC-04
  - Métodos: `isSemestreOcupado`, `grupoUsaHoraInicio`, `sesionesEnFranja`, `sesionesEnSemestreDia`

- ✅ **Randomización en el motor**:
  - `ordenarPorPrioridad`: shuffle antes del sort para variar el orden dentro de grupos de igual prioridad
  - `seleccionarMejor`: reúne todos los candidatos con el puntaje máximo y elige uno al azar

- ✅ **`identificarHCCausa`** actualizado para diagnosticar HC-12 y HC-13

- ✅ **`ConflictoDTO`** — descripciones y acciones correctivas para HC-11, HC-12, HC-13

### Estructura de franjas (tabla `franja_horaria`)
```
Lun–Vie: 07-09, 09-11, (11-13 inválida), 13-15, 15-17, 17-19, 19-21  → 6 válidas/día
Sábado:  07-09, 09-11, (11-13 inválida)                               → 2 válidas
Total válidas: 32 franjas
```

### Archivos del motor (ruta base: backend/src/main/java/.../service/algorithm/)
```
HorarioContexto.java          — estado mutable en memoria
SchedulerEngine.java          — ciclo greedy + repair pass + randomización
evaluator/
  SinTraslapeDocenteEvaluator.java       HC-01
  SinTraslapeAulaEvaluator.java          HC-02
  DisponibilidadDocenteEvaluator.java    HC-03
  TipoAulaCompatibleEvaluator.java       HC-04
  CapacidadAulaEvaluator.java            HC-05
  FranjaHorariaPermitidaEvaluator.java   HC-06
  ExclusionMediodiaEvaluator.java        HC-07
  DuracionFijaEvaluator.java             HC-08
  FrecuenciaSemanalEvaluator.java        HC-09
  CompatibilidadDocenteCursoEvaluator.java HC-10
  FranjaEnRangoCursoEvaluator.java       HC-11 (ventana horaria curso)
  SinTraslapesSemestreEvaluator.java     HC-12 (sin solapamiento de semestre) ← NUEVO
  SinRepeticionBloqueGrupoEvaluator.java HC-13 (bloques únicos por grupo)     ← NUEVO
  SesionesNoConsecutivasSoftEvaluator.java SC-01
  DistribucionCargaSoftEvaluator.java      SC-02
  DistribucionFranjasSoftEvaluator.java    SC-03 (distribución global franjas) ← NUEVO
  DistribucionSemestreDiaSoftEvaluator.java SC-04 (equilibrio semestre-día)   ← NUEVO
```

---

## Fase 4 — WebSocket

- ✅ WebSocketConfig, AsignacionWebSocketController, validar y confirmar movimiento

### Corrección aplicada en sesión 2026-05-02

- ✅ **Bug `__pending__` en CalendarioSemanal.jsx** — `onValidacionRecibida` guardaba resultado
  bajo clave literal `"__pending__"` pero las celdas buscaban por `celdaId`. Fix: se añadió
  `overCeldaIdRef = useRef(null)` que se actualiza en `onDragOver` y se usa como clave en el callback.

---

## Fase 5 — Frontend React

### Correcciones aplicadas en sesión 2026-05-02

- ✅ **GruposPage.jsx** — eliminada columna `semestre` (no existe en GrupoDTO);
  añadido botón "Cierre automático" que llama `POST /api/grupos/cerrar-automatico`.

- ✅ **grupoService.js** — `cerrar()` ahora incluye body `{}` (backend requiere @RequestBody);
  añadido `cerrarAutomatico()`.

- ✅ **GrupoService.java** — `actualizar()` ahora cierra automáticamente el grupo si
  `numInscritos < umbralCierre` (parámetro de semestre), con causa `BAJA_INSCRIPCION_AUTOMATICA`.

- ✅ **DocentesPage.jsx** — modal de compatibilidades rediseñado: tabla con columnas Código/Curso/🗑️,
  scroll vertical, select muestra `código — nombre`, botón eliminar por fila.

- ✅ **docenteService.js** — añadido `eliminarCompatibilidad(id, cursoId)`.

- ✅ **HorarioGenerarPage.jsx** — límites corregidos: MIN=1, MAX=81;
  selector de cursos muestra lista completa con búsqueda y contador claro.

---

## Datos Maestros — Disponibilidades de Docentes

### Perfiles actuales (actualizados 2026-05-03)

| Perfil | Docentes | Horario | Bloques disponibles |
|--------|----------|---------|---------------------|
| **Diurno** | 3, 7, 13, 19, 20, 21, 24 | Lun-Jue o Lun-Vie 07:00–17:00 | 07-09, 09-11, 13-15, 15-17 |
| **Estándar-noche** | 1, 2, 4, 5, 6, 8, 11, 12 | Lun-Vie 07:00–**21:00** | + bloques 17-19, **19-21** |
| **Estándar** | 14, 17, 18, 22, 23 | Lun-Vie 07:00–19:00 | + bloque 17-19 |
| **Mañana** | 10 | Lun-Vie 07:00–15:00 | 07-09, 09-11, 13-15 |
| **Mañana tardía** | 15 | Lun-Vie 08:00–15:00 | 09-11, 13-15 |
| **Noche** | 9, 25 | 3 días 14:00–21:00 | 15-17, 17-19, **19-21** |
| **Sábado** | 3, 4, 5, 7, 8, 16, 22 | Sáb 07:00–13:00 | sáb 07-09, sáb 09-11 |

**Cambio 2026-05-03**: docs 1,2,4,5,6,8,11,12 extendidos de 19:00 a **21:00** (nocturno).
Docs 3,4,5,7,8,22 añadidos con disponibilidad sábado (además del existente 16).
Motivo: con 8+ grupos el motor necesita ≥10 docentes nocturnos y ≥7 de sábado para resolver sin conflictos y permitir drag-and-drop.

**Lógica de `cubre(franja)`**: `franja.horaInicio >= disp.horaInicio AND franja.horaValida <= disp.horaFin`
- Para enseñar el bloque 17:00–19:00 se necesita `horaFin >= 19:00`
- Para enseñar el bloque 19:00–21:00 se necesita `horaFin >= 21:00`

---

## Registro de Decisiones Técnicas

| Fecha | Decisión | Motivo |
|-------|----------|--------|
| 2026-04-29 | Lombok subido a 1.18.36 | Java 23 no compatible con 1.18.30 |
| 2026-04-29 | CorsConfig usa setAllowedOriginPatterns | setAllowedOrigins("*") incompatible con allowCredentials=true |
| 2026-04-30 | Hash BCrypt corregido (admin123) | Hash anterior era para "password" |
| 2026-04-30 | HorarioContexto con 2 constructores | Tests unitarios (vacío); motor producción (pre-cargado) |
| 2026-04-30 | ResultadoGeneracion como record | Motor no persiste; HorarioService convierte a entidades JPA |
| 2026-05-01 | Franjas generadas con PL/pgSQL en BD | seed.sql no pasa por servicio que las genera |
| 2026-05-02 | break→continue en ciclo greedy | break abandonaba grupo entero al primer fallo de sesión |
| 2026-05-02 | HC-12 antes de HC-13 | Semestre es constraint estructural; bloques únicos es refinamiento |
| 2026-05-02 | SC-03 penalización=20 (no 8) | Con ~5 semestres activos, score llega a 0 forzando variedad real |
| 2026-05-02 | Disponibilidades corregidas (hora_fin) | 20:00 permitía enseñar tarde sin querer; 21:00 faltaba para noche |
| 2026-05-03 | 8 docs extendidos a 21:00; 6 docs añadidos a sábado | Con 8+ grupos y HC-01/12/13, solo 2 docs nocturnos era insuficiente para resolver y editar |

---

## Errores Conocidos Resueltos

| Error | Archivo | Resuelto |
|-------|---------|----------|
| `break` prematuro en greedy | SchedulerEngine.java | ✅ 2026-05-02 |
| Docente/semestre sin traslape de semestre | (no existía HC) | ✅ HC-12 creado 2026-05-02 |
| Mismo bloque horario repetido en un grupo | (no existía HC) | ✅ HC-13 creado 2026-05-02 |
| Sesiones acumuladas en mismas franjas | (SC insuficientes) | ✅ SC-03/SC-04 creados 2026-05-02 |
| WebSocket feedback se perdía en drop | CalendarioSemanal.jsx | ✅ overCeldaIdRef 2026-05-02 |
| Docentes con disponibilidad incorrecta | disponibilidad_docente | ✅ 2026-05-02 |
| Columna semestre inexistente en GruposPage | GruposPage.jsx | ✅ 2026-05-02 |
| Bloque 19:00-21:00 no asignable a nadie | disponibilidad_docente | ✅ doc 9 y 25 con hora_fin=21:00 |

---

## Próximo Paso

```
SESIÓN 2026-05-02 — Completado:
✅ Motor Greedy corregido y ampliado (HC-11, HC-12, HC-13, SC-03, SC-04)
✅ Frontend corregido (grupos, docentes, generación de horarios)
✅ Disponibilidades de docentes corregidas en BD y seed.sql
✅ Compilación limpia (mvn compile sin errores)

ESTADO ACTUAL DEL SISTEMA:
- Backend: listo, compilado, sin errores conocidos
- Frontend: listo, correcciones aplicadas
- BD: poblada con datos realistas (25 docentes, 81 cursos, 48 grupos, 32 franjas válidas)

TAREA INMEDIATA AL INICIO DE LA SIGUIENTE SESIÓN:

1. LEVANTAR EL SISTEMA:
   # Backend (terminal 1):
   $env:PATH = "C:\apache-maven-3.9.6\bin;$env:PATH"
   cd D:\ProyectoNucleo\backend
   .\mvnw.cmd spring-boot:run

   # Frontend (terminal 2):
   cd D:\ProyectoNucleo\frontend
   npm run dev

2. PROBAR GENERACIÓN DE HORARIOS:
   - Ir a http://localhost:5173/login → admin / admin123
   - Ir a /admin/horario/generar
   - Seleccionar cursos de UN SEMESTRE (ej: semestre 1)
   - Verificar que no hay solapamientos (HC-12)
   - Seleccionar cursos de VARIOS SEMESTRES
   - Verificar que las sesiones de cada grupo están en bloques horarios distintos (HC-13)
   - Verificar que la distribución es equilibrada entre días y franjas

3. VERIFICAR EN CALENDARIO (/admin/horario):
   - Ver que las asignaciones muestran hora_inicio – hora_valida correctamente
   - Probar drag-and-drop y verificar feedback visual

4. INICIAR FASE 6 — Pruebas y Aceptación:
   Ver specs/tests.md para los casos de prueba requeridos.
   a) Tests de integración (@SpringBootTest + H2):
      - AuthController: login ok, login fallo, token expirado
      - PublicoController: GET /api/publico/horario/2026-1 sin token → 200
      - DocenteController: CRUD completo con token admin
   b) Test de rendimiento: 30 grupos en < 60 segundos (SchedulerEngine)
   c) Tests de seguridad: sin token → 401, token docente en /admin → 403

NOTAS IMPORTANTES:
- Los HC nuevos (12, 13) pueden causar más conflictos en semestres con muchos grupos.
  Si aparecen muchos HC-12: indica que el semestre tiene más grupos que franjas disponibles.
  Si aparecen muchos HC-13: indica que el docente no tiene suficientes bloques distintos declarados.
  Solución: ampliar disponibilidades del docente en el módulo /admin/docentes.
- El bloque 19:00-21:00 solo lo pueden cubrir doc 9 (Ricardo Peña) y doc 25 (Iván Guzmán).
  Si se generan grupos con esos docentes, se usará el bloque nocturno.
```
