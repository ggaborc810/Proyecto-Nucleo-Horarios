# Sistema Web — Generación Automática de Horarios Académicos

Sistema fullstack para el Programa de Ingeniería de Sistemas de la Universidad El Bosque. Resuelve el *University Course Timetabling Problem* (NP-hard) usando un **algoritmo Greedy con función de evaluación escalonada**.

## Stack

- **Backend**: Java 17 + Spring Boot 3 + PostgreSQL 14+
- **Frontend**: React 19 + Vite + Tailwind
- **Realtime**: WebSocket (STOMP) para validación drag-and-drop
- **Auth**: Spring Security + JWT
- **Tests**: JUnit 5 + Mockito (motor) / MockMvc (integración)

## Reglas Absolutas — NUNCA Violar

1. **Cero constantes de dominio en código fuente.** Franjas, capacidades, umbrales: TODO en `ParametroSemestre`. (RNF-07)
2. **Capa de Dominio sin Spring/JPA.** El motor Greedy se prueba con solo Mockito, sin `@SpringBootTest`, en < 10s.
3. **Motor opera 100% en memoria.** Carga inicial única, cero queries durante el ciclo de asignación. (RNF-01)
4. **Toda solución es válida en HC, o reporta conflicto.** Nunca asignación silenciosa con HC violado.
5. **Vista pública sin auth (200).** Rutas admin sin JWT → 401. JWT con rol insuficiente → 403. (RNF-04)
6. **Parada del algoritmo**: máx 10.000 iteraciones O 60s CPU.

## Estructura del Repo

```
proyecto/
├── CLAUDE.md                         # Este archivo (índice)
├── backend/                          # Spring Boot 3
├── frontend/                         # React 19
├── docs/                             # Diseño y arquitectura
│   ├── architecture.md               # 4 capas, patrones
│   ├── database.md                   # MER + DDL
│   ├── implementation-plan.md        # Orden de implementación
│   ├── algorithm/
│   │   ├── overview.md               # Motor Greedy
│   │   ├── hc-evaluators.md          # HCs base; el codigo actual incluye HC-01 a HC-13
│   │   ├── soft-evaluators.md        # SC base; el codigo actual incluye SC-01 a SC-04
│   │   └── pseudocode.md             # Algoritmo paso a paso
│   ├── backend/
│   │   ├── entities.md               # Entidades JPA
│   │   ├── repositories.md           # Interfaces JpaRepository
│   │   ├── services.md               # Servicios de aplicación
│   │   ├── controllers.md            # REST + WebSocket
│   │   ├── security.md               # JWT + Spring Security
│   │   └── config.md                 # application.yml + dependencias
│   └── frontend/
│       ├── design-system.md          # Paleta + tipografía + variables CSS
│       ├── components.md             # Estructura de componentes
│       ├── routing.md                # React Router + auth guards
│       ├── calendar.md               # Vista calendario + drag-and-drop
│       └── websocket.md              # Cliente STOMP
└── specs/                            # Contratos vivos
    ├── api.md                        # Endpoints REST + WebSocket
    └── tests.md                      # Casos de prueba + aceptación
```

## Restricciones del Dominio (Resumen)

**Hard Constraints (HC) — irrompibles:**

| ID | Restricción |
|----|-------------|
| HC-01 | Docente no puede tener dos sesiones en mismo día+franja |
| HC-02 | Aula no puede tener dos sesiones en mismo día+franja |
| HC-03 | Sesión solo en franjas de disponibilidad declarada del docente |
| HC-04 | Tipo de aula = tipo requerido por curso |
| HC-05 | Capacidad aula ≥ inscritos del grupo |
| HC-06 | Franja válida (Lun-Vie 07-22, Sáb 07-13, parametrizable) |
| HC-07 | Excluir 12:00-13:00 (parametrizable) |
| HC-08 | Sesión = exactamente 2 horas |
| HC-09 | Cada grupo recibe `frecuencia_semanal` sesiones exactas |
| HC-10 | Solo docentes en `CompatibilidadDocenteCurso` |
| HC-11 | Ventana horaria permitida por curso |
| HC-12 | Grupos del mismo semestre no se traslapan en la misma franja |
| HC-13 | Un grupo no repite el mismo bloque horario en distintos dias |

**Soft Constraints (SC) — solo desempate:** SC-01 (sesiones no consecutivas), SC-02 (carga distribuida), SC-03 (distribucion global de franjas), SC-04 (distribucion por semestre y dia).

**Función de prioridad (P1 > P2 > P3 > P4):** mayor inscritos → mayor semestre → menos bloques alternativos (MRV) → orden original.

## Por Dónde Empezar

1. Lee `docs/implementation-plan.md` — orden de fases
2. Lee `docs/architecture.md` — entender las 4 capas
3. Lee `docs/database.md` — crear el esquema
4. Implementa por fases siguiendo el plan

## MVP

**Incluye**: CRUD datos maestros, motor Greedy, reporte conflictos, calendario drag-and-drop, vista pública, apertura/cierre de grupos.

**Excluye**: inscripción de estudiantes, integración SGA/nómina, dashboards avanzados, multi-sede, notificaciones por email.

## Convenciones de Código

- **Java**: Lombok permitido (`@Data`, `@Builder`). Records para DTOs.
- **React**: componentes funcionales con hooks. Sin clases.
- **Commits**: `feat:`, `fix:`, `refactor:`, `test:`, `docs:`.
- **Tests primero** en HCEvaluators y motor. Después en servicios.
