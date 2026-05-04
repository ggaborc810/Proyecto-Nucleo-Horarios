# Motor Greedy — Visión General

## Problema

El sistema resuelve el *University Course Timetabling Problem* (NP-hard). No buscamos solución óptima global: construimos una solución válida (cumple todos los HC) usando heurística determinista. Si no existe solución, reportamos conflicto con HC violado identificado.

## Estrategia

**Algoritmo Greedy con función de evaluación escalonada**, seleccionado sobre metaheurísticas (genéticos, tabú, recocido) por:

1. **Determinismo controlado**: cada decisión es localmente óptima según función documentada y auditable.
2. **Filtros previos**: las HC actúan como filtros antes de evaluar SC, evitando backtracking extensivo.
3. **Reporte inmediato de conflictos**: cuando ningún bloque válido está disponible, se registra el HC que agotó las opciones.

## Función de Priorización (P1 > P2 > P3 > P4)

Cuando dos grupos compiten por el mismo recurso, se asigna primero al que tenga:

| Nivel | Criterio | Justificación |
|-------|----------|--------------|
| P1 | Mayor número de inscritos | Mayor impacto si queda sin asignar |
| P2 | Mayor semestre del curso | Cursos avanzados tienen menor flexibilidad |
| P3 | Menor número de bloques alternativos (MRV) | Reduce riesgo de quedar sin solución |
| P4 | Orden original | Desempate final |

**P3 (MRV — Minimum Remaining Values)** es la heurística clásica: se atiende primero al grupo con menos opciones disponibles, porque si lo dejamos para el final probablemente no quede ningún espacio.

## Criterios de Parada

| Criterio | Default | Configurable |
|----------|---------|--------------|
| Iteraciones máximas | 10.000 | Sí (`application.yml`) |
| Tiempo máximo CPU | 60 segundos | Sí |

Al alcanzar cualquiera, se detiene el ciclo, se persisten asignaciones logradas y se genera reporte de conflictos para las pendientes.

## Componentes Clave

- **`SchedulerEngine`**: orquesta todo el algoritmo
- **`HCEvaluator[]`**: 10 clases que validan HC-01 a HC-10 (Strategy)
- **`SoftEvaluator[]`**: 2 clases que puntúan SC-01 y SC-02 para desempate
- **`HorarioContexto`**: estado mutable en memoria durante generación
- **`AsignacionCandidato`**: value object inmutable que el motor evalúa

## Carga en Memoria — Por Qué

El motor opera **completamente en memoria** durante el ciclo. Carga inicial única:

```
parametros        ← ParametroSemestreRepository.findBySemestre(...)
grupos            ← GrupoRepository.findByEstado('ACTIVO')
franjas           ← FranjaHorarioRepository.findByEsValidaTrue()
docentes          ← DocenteRepository.findAll()  // incluye disponibilidades
aulas             ← AulaRepository.findByActivaTrue()
compatibilidades  ← CompatibilidadRepository.findAll()
```

Después, **cero queries** durante el ciclo de asignación. Esto es lo que hace cumplir el RNF-01 (30 grupos en < 60s).

## Escenario de Falla

Si el motor no logra asignar todas las sesiones:
1. **No falla silenciosamente** — registra el conflicto.
2. Identifica el **HC específico** que impidió la asignación.
3. Sugiere **acciones correctivas** comprensibles para el administrador.
4. Continúa con las siguientes sesiones (no entra en bucle).

Más detalle en:
- `docs/algorithm/hc-evaluators.md` — implementación de cada HC
- `docs/algorithm/soft-evaluators.md` — SC-01 y SC-02
- `docs/algorithm/pseudocode.md` — pseudocódigo completo
