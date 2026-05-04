# Arquitectura

## 4 Capas con Dependencia Unidireccional

```
┌──────────────────────────────────────────────────┐
│  Presentación (React 19)                         │
│  Vista Calendario · Vista Pública · Panel Admin  │
└────────────────────┬─────────────────────────────┘
                     │ HTTP REST + WebSocket (STOMP)
┌────────────────────▼─────────────────────────────┐
│  Aplicación (Spring Boot 3)                      │
│  REST Controllers · JWT Security · WebSocket     │
└────────────────────┬─────────────────────────────┘
                     │
┌────────────────────▼─────────────────────────────┐
│  Dominio (Java puro — SIN infraestructura)       │
│  Motor Greedy · HCEvaluator · Servicios          │
└────────────────────┬─────────────────────────────┘
                     │
┌────────────────────▼─────────────────────────────┐
│  Persistencia (Spring Data JPA / Hibernate)      │
│  Repositorios → PostgreSQL 14+                   │
└──────────────────────────────────────────────────┘
```

**Regla de Oro**: Si reemplazamos PostgreSQL por archivos JSON, solo la Capa de Persistencia cambia. La Capa de Dominio nunca lo nota porque consume interfaces (`DocenteRepository`, `HorarioRepository`, etc.) definidas en el dominio.

## Patrones de Diseño Aplicados

### Strategy — HCEvaluator
Cada Hard Constraint se implementa como una clase independiente que implementa la interfaz `HCEvaluator`. La documentacion base describe HC-01 a HC-10 y el codigo actual agrega HC-11, HC-12 y HC-13.

**Beneficio**: agregar una nueva HC = crear una clase nueva e incorporarla a la lista de evaluadores. Cero cambios estructurales en el motor.

### Observer — Validación Drag-and-Drop
La vista de calendario (Subject) emite eventos al servidor por WebSocket. El servidor (Observer) invoca `HCEvaluator` y devuelve resultado. La lógica de dominio queda en el servidor.

**Beneficio**: la lógica HC no se duplica en el frontend.

### Repository — Abstracción de Persistencia
Las interfaces de repositorio se definen en la Capa de Dominio. Spring Data JPA las implementa en runtime; Mockito las simula en tests.

**Beneficio**: el motor Greedy se testea sin BD activa.

## Componentes Principales

| Componente | Capa | Responsabilidad |
|-----------|------|-----------------|
| `SchedulerEngine` | Dominio | Orquesta el algoritmo Greedy |
| `HCEvaluator[]` | Dominio | Validan restricciones (Strategy) |
| `SoftEvaluator[]` | Dominio | Calculan score para desempate |
| `HorarioContexto` | Dominio | Estado en memoria durante generación |
| `HorarioService` | Aplicación | Orquesta generación + persistencia |
| `AsignacionService` | Aplicación | Validar movimientos drag-and-drop |
| `GrupoService` | Aplicación | Cierre automático de grupos |
| `*Controller` | Aplicación | Endpoints REST |
| `AsignacionWebSocketController` | Aplicación | Validación tiempo real |
| `*Repository` | Persistencia | Acceso a PostgreSQL |

## Coherencia Arquitectura ↔ Base de Datos

1. **Doble validación HC-01/HC-02**: motor valida + BD refuerza con `UNIQUE(docente_id, franja_id)` y `UNIQUE(aula_id, franja_id)`.
2. **RNF-07 cumplido**: todos los parámetros viven en `parametro_semestre`. El motor los lee al inicio.
3. **1 entidad ↔ 1 tabla ↔ 1 repositorio**: facilita el patrón Repository.
4. **Índices B-Tree**: garantizan verificaciones O(log n) en `asignacion`.

## Estilo de Prototipado

- **Infraestructura, BD, UI**: Evolutionary (código de calidad producción desde el inicio).
- **Motor Greedy**: Throwaway durante Fase 2 (validar con Director del Programa) → Evolutionary desde Fase 3.

## Por qué este stack

| Tecnología | Justifica RNF |
|-----------|--------------|
| Java 17 LTS | RNF-06 (portabilidad) |
| Spring Boot 3 + DI | RNF-05 (motor sin Spring en tests) |
| Spring Security + JWT | RNF-04 (auth sin sesión) |
| Spring WebSocket (STOMP) | RF-08 (drag-and-drop tiempo real) |
| Spring Data JPA | RNF-05 (mockable) |
| PostgreSQL 14+ | RNF-01 (índices B-Tree O(log n)) |
| React 19 | Componentes reutilizables, drag-and-drop nativo |
| JUnit 5 + Mockito | RNF-05 (suite < 10s sin BD) |

Más detalle en `docs/backend/config.md`.
