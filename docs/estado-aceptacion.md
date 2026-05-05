# Estado de aceptacion - 2026-05-04

Este documento registra la revision del estado real del sistema contra `CLAUDE.md`, `docs/progress.md`, `docs/implementation-plan.md`, `specs/api.md` y `specs/tests.md`.

## Resultado ejecutivo

El sistema esta implementado hasta Fase 5 y compila. La Fase 6 sigue abierta porque faltan pruebas automatizadas de integracion, seguridad, rendimiento y aceptacion funcional completa.

## Prueba ejecutada el 2026-05-04

Comandos ejecutados:

- `backend`: `.\mvnw.cmd test`
- `frontend`: `npm.cmd run build`
- `backend dev`: `.\mvnw.cmd spring-boot:run`
- `frontend dev`: `npm.cmd run dev -- --host 127.0.0.1`

Resultados:

- Backend tests: OK, 52 tests, 0 fallos, 0 errores.
- Frontend build: OK, 185 modulos transformados, build generado en `frontend/dist`.
- Backend dev: OK, Tomcat inicio en `http://localhost:8080` y conecto a PostgreSQL.
- Frontend dev: OK, Vite inicio en `http://127.0.0.1:5173/`.
- `GET http://localhost:5173`: 200.
- `GET http://localhost:8080/api/publico/horario/2026-1`: 200 sin token.
- `GET http://localhost:8080/api/docentes` sin token: 401.
- `POST http://localhost:8080/api/auth/login` con `admin/admin123`: OK, rol `ADMIN`.
- `GET http://localhost:8080/api/auth/me` con JWT: OK, usuario `admin`, rol `ADMIN`.
- `GET http://localhost:8080/api/docentes` con JWT admin: 200.

No se ejecuto `POST /api/horarios/generar` porque el servicio borra las asignaciones previas del horario del semestre antes de regenerar. Esa prueba debe hacerse de forma intencional cuando se acepte modificar datos de la BD de desarrollo.

## Arranque con Docker

El proyecto ahora incluye `docker-compose.yml` en la raiz con:

- `postgres`
- `backend`
- `frontend`

El frontend se expone en `http://localhost:5173` y proxya `/api` y `/ws` contra el backend.
El backend toma `DB_URL=jdbc:postgresql://postgres:5432/horarios_db`, por lo que la BD se crea y se migra al levantar el stack.

## Verificado en codigo

- Backend Spring Boot presente en `backend/`.
- Frontend React/Vite presente en `frontend/`.
- Migraciones automáticas configuradas con Flyway en `backend/src/main/resources/db/migration/`.
- Motor Greedy implementado en `backend/src/main/java/co/edu/unbosque/horarios/service/algorithm/`.
- Evaluadores HC implementados de HC-01 a HC-13.
- Evaluadores SC implementados de SC-01 a SC-04.
- WebSocket STOMP implementado en `backend/src/main/java/co/edu/unbosque/horarios/websocket/`.
- CRUD principal y servicios de aplicacion presentes.
- Seguridad JWT configurada en `SecurityConfig`.
- Vista publica sin autenticacion configurada para `/api/publico/**`.

## Pruebas disponibles

- Tests unitarios del motor para HC-01 a HC-10.
- Test unitario para HC-13.
- Tests unitarios de soft evaluators.
- Test de carga de contexto Spring Boot.

## Faltantes para cerrar Fase 6

1. Crear tests MockMvc de integracion:
   - `POST /api/auth/login` exitoso y fallido.
   - `GET /api/publico/horario/{semestre}` sin token.
   - Rutas admin sin token deben retornar 401.
   - JWT con rol insuficiente debe retornar 403.
   - CRUD basico de docentes con token admin.
   - Generacion de horario via `POST /api/horarios/generar`.
   - Movimiento de asignacion valido e invalido.

2. Crear tests de rendimiento:
   - Generar 30 grupos en menos de 60 segundos.
   - Verificar que el motor termina en casos imposibles.

3. Completar tests unitarios del motor:
   - `SchedulerEngineTest` con caso completo, caso imposible y prioridad P1/P2/P3/P4.
   - Tests explicitos para HC-11.
   - Tests explicitos para HC-12.

4. Ejecutar prueba manual con el programa levantado:
   - Login `admin/admin123`.
   - Generar horario para un semestre.
   - Generar horario para varios semestres.
   - Revisar calendario admin.
   - Probar drag-and-drop con feedback visual.
   - Publicar horario sin conflictos.
   - Revisar vista publica.

## Desviaciones documentales corregidas

- El stack de frontend se documento como React 19 porque `frontend/package.json` usa React 19.
- Se actualizo el indice para reflejar HC-01 a HC-13 y SC-01 a SC-04.
- Se agregaron acciones correctivas para HC-11, HC-12 y HC-13 en `specs/api.md`.
- La inicializacion de BD paso de scripts manuales a migraciones automáticas Flyway.
- El arranque completo ya puede hacerse con Docker Compose en un solo comando.

## Desviaciones pendientes de decidir

- `specs/api.md` documenta filtros publicos por aula y curso, pero `PublicoController` solo implementa horario completo, horario por docente y grupos publicos. Se debe decidir si se implementan esos endpoints o si se corrige la especificacion.
- `docs/reporte-progreso.html` parece desactualizado frente al codigo actual y a `docs/progress.md`.

## Cambios de logica

No se hicieron cambios de logica ni se agregaron funcionalidades en esta revision documental.
