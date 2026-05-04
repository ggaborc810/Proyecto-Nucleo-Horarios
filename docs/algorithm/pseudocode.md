# Pseudocódigo del Motor

## Algoritmo Principal

```
ALGORITMO GenerarHorario(semestre: String) → ResultadoGeneracion

    // ── FASE 1: CARGA EN MEMORIA (única, antes del ciclo) ──────────
    parametros        ← ParametroSemestreRepository.findBySemestre(semestre)
    grupos            ← GrupoRepository.findByEstado('ACTIVO')
    franjas           ← FranjaHorarioRepository.findByEsValidaTrue()
    docentes          ← DocenteRepository.findAll()  // con disponibilidades
    aulas             ← AulaRepository.findByActivaTrue()
    compatibilidades  ← CompatibilidadRepository.findAll()  // a Set<DocenteCursoKey>

    contexto      ← new HorarioContexto()
    conflictos    ← []
    asignaciones  ← []
    iteraciones   ← 0
    tiempoInicio  ← now()

    // ── FASE 2: ORDENAR GRUPOS POR PRIORIDAD (P1→P2→P3→P4) ─────────
    grupos ← ordenarPorPrioridad(grupos, franjas, aulas, contexto)

    // ── FASE 3: CICLO DE ASIGNACIÓN ────────────────────────────────
    PARA CADA grupo EN grupos:
        sesionesRequeridas ← grupo.curso.frecuencia_semanal
        sesionesAsignadas  ← 0

        MIENTRAS sesionesAsignadas < sesionesRequeridas:
            iteraciones ← iteraciones + 1

            // Criterio de parada
            SI iteraciones > 10000 O (now() - tiempoInicio) > 60s:
                conflictos.agregar(ConflictoParada(grupo, sesionesRequeridas - sesionesAsignadas))
                IR A siguienteGrupo

            // Construir candidatos válidos: combinaciones (franja × aula) que pasan TODOS los HC
            candidatos ← []
            PARA CADA franja EN franjas:
                PARA CADA aula EN aulas:
                    candidato ← new AsignacionCandidato(grupo, grupo.docente, aula, franja)
                    valido ← true

                    PARA CADA evaluador EN hcEvaluators:  // 10 HCs en orden
                        SI NO evaluador.evaluate(candidato, contexto):
                            valido ← false
                            ROMPER

                    SI valido:
                        candidatos.agregar(candidato)

            SI candidatos.isEmpty():
                hcCausa ← identificarHCCausa(grupo, franjas, aulas, contexto)
                conflictos.agregar(new ConflictoAsignacion(grupo, sesionesAsignadas + 1, hcCausa))
                ROMPER  // pasar al siguiente grupo

            // Aplicar SC como desempate
            mejor ← seleccionarMejorCandidato(candidatos, contexto)

            // Confirmar asignación
            asignacion ← new Asignacion(mejor, horarioId)
            asignaciones.agregar(asignacion)
            contexto.registrarAsignacion(mejor)
            sesionesAsignadas ← sesionesAsignadas + 1

    // ── FASE 4: PERSISTIR RESULTADOS ───────────────────────────────
    AsignacionRepository.saveAll(asignaciones)
    SI conflictos.notEmpty():
        ConflictoRepository.saveAll(conflictos)

    RETORNAR new ResultadoGeneracion(asignaciones, conflictos, tiempoMs, iteraciones)
```

## Subrutinas

### ordenarPorPrioridad

```
FUNCIÓN ordenarPorPrioridad(grupos, franjas, aulas, contexto) → List<Grupo>

    PARA CADA grupo EN grupos:
        grupo.bloquesAlternativos ← contar combinaciones (franja × aula) que pasan HC
            SIN considerar el contexto (precomputado para MRV)

    RETORNAR grupos.ordenarPor(
        descendente: g.numInscritos,            // P1
        descendente: g.curso.semestreNivel,     // P2
        ascendente:  g.bloquesAlternativos,     // P3 (MRV)
        ascendente:  g.grupoId                  // P4
    )
```

### identificarHCCausa

```
FUNCIÓN identificarHCCausa(grupo, franjas, aulas, contexto) → String

    // Recorrer cada HC y diagnosticar cuál agotó las opciones
    SI no existen aulas del tipo requerido por curso → "HC-04"
    SI no existen aulas con capacidad ≥ inscritos → "HC-05"
    SI no quedan franjas válidas con docente disponible → "HC-03"
    SI todas las franjas del docente están ocupadas en contexto → "HC-01"
    SI todas las aulas están ocupadas en franjas restantes → "HC-02"
    SI docente no tiene compatibilidad con curso → "HC-10"
    DEFAULT → "INFACTIBLE"  // múltiples HC simultáneos
```

### seleccionarMejorCandidato

```
FUNCIÓN seleccionarMejorCandidato(candidatos, contexto) → AsignacionCandidato

    PARA CADA candidato EN candidatos:
        score ← 0
        PARA CADA softEvaluator EN softEvaluators:
            score ← score + softEvaluator.score(candidato, contexto)
        candidato.totalScore ← score

    RETORNAR candidatos.maxBy(c → c.totalScore)
```

## Esqueleto Java del SchedulerEngine

```java
@Component
public class SchedulerEngine {

    private final List<HCEvaluator> hcEvaluators;
    private final List<SoftEvaluator> softEvaluators;
    private final DocenteRepository docenteRepo;
    private final AulaRepository aulaRepo;
    private final FranjaHorarioRepository franjaRepo;
    private final GrupoRepository grupoRepo;
    private final ParametroSemestreRepository paramRepo;
    private final CompatibilidadDocenteCursoRepository compatRepo;

    @Value("${app.scheduler.max-iteraciones:10000}")
    private int maxIteraciones;
    @Value("${app.scheduler.timeout-segundos:60}")
    private int timeoutSegundos;

    public ResultadoGeneracion ejecutar(String semestre, Integer horarioId) {
        long inicio = System.currentTimeMillis();

        // Fase 1: carga
        ParametroSemestre params = paramRepo.findBySemestre(semestre).orElseThrow();
        List<Grupo> grupos      = grupoRepo.findByEstado("ACTIVO");
        List<FranjaHoraria> franjas = franjaRepo.findByEsValidaTrue();
        List<Aula> aulas        = aulaRepo.findByActivaTrue();
        // ... carga adicional

        // Fase 2: ordenar
        grupos = ordenarPorPrioridad(grupos, franjas, aulas);

        // Fase 3: ciclo
        HorarioContexto contexto = new HorarioContexto();
        List<Asignacion> asignaciones = new ArrayList<>();
        List<ConflictoAsignacion> conflictos = new ArrayList<>();
        int iteraciones = 0;

        for (Grupo grupo : grupos) {
            int requeridas = grupo.getCurso().getFrecuenciaSemanal();
            int asignadas  = 0;

            while (asignadas < requeridas) {
                iteraciones++;
                if (iteraciones > maxIteraciones || tiempoExcedido(inicio)) {
                    conflictos.add(ConflictoAsignacion.parada(grupo, requeridas - asignadas));
                    break;
                }

                List<AsignacionCandidato> candidatos = construirCandidatos(grupo, franjas, aulas, contexto);
                if (candidatos.isEmpty()) {
                    String hcCausa = identificarHCCausa(grupo, franjas, aulas, contexto);
                    conflictos.add(new ConflictoAsignacion(grupo, asignadas + 1, hcCausa));
                    break;
                }

                AsignacionCandidato mejor = seleccionarMejor(candidatos, contexto);
                asignaciones.add(toAsignacion(mejor, horarioId));
                contexto.registrarAsignacion(mejor);
                asignadas++;
            }
        }

        long tiempoMs = System.currentTimeMillis() - inicio;
        return new ResultadoGeneracion(asignaciones, conflictos, tiempoMs, iteraciones);
    }

    private boolean tiempoExcedido(long inicio) {
        return (System.currentTimeMillis() - inicio) > (timeoutSegundos * 1000L);
    }

    // ordenarPorPrioridad, construirCandidatos, identificarHCCausa, seleccionarMejor, toAsignacion
}
```

## Estructuras de Datos del Resultado

```java
public record ResultadoGeneracion(
    List<Asignacion> asignacionesExitosas,
    List<ConflictoAsignacion> conflictos,
    long tiempoEjecucionMs,
    int totalIteraciones
) {
    public boolean esCompleto() { return conflictos.isEmpty(); }
}

public record ConflictoAsignacion(
    Grupo grupo,
    int sesionNumero,
    String hcViolado,
    String descripcion,
    List<String> accionesCorrectivas
) { ... }
```

## Acciones Correctivas Sugeridas (mapeo HC → mensaje)

| HC | Acción Correctiva |
|----|-------------------|
| HC-01 | "Asignar otro docente compatible con el curso" |
| HC-02 | "Agregar más aulas del tipo requerido" |
| HC-03 | "Ampliar disponibilidad horaria del docente" |
| HC-04 | "Verificar que existan aulas activas del tipo requerido" |
| HC-05 | "Aumentar capacidad del aula o dividir el grupo" |
| HC-06 | "Revisar parámetros de franja horaria" |
| HC-07 | "Ajustar la franja de exclusión de mediodía" |
| HC-09 | "Reducir frecuencia semanal o agregar más recursos" |
| HC-10 | "Registrar compatibilidad docente-curso" |
