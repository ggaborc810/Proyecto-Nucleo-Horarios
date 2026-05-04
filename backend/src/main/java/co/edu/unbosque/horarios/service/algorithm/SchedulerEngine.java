package co.edu.unbosque.horarios.service.algorithm;

import co.edu.unbosque.horarios.model.*;
import co.edu.unbosque.horarios.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SchedulerEngine {

    private final List<HCEvaluator>   hcEvaluators;
    private final List<SoftEvaluator> softEvaluators;
    private final DocenteRepository   docenteRepo;
    private final AulaRepository      aulaRepo;
    private final FranjaHorarioRepository franjaRepo;
    private final GrupoRepository     grupoRepo;
    private final ParametroSemestreRepository paramRepo;
    private final CompatibilidadDocenteCursoRepository compatRepo;

    @Value("${app.scheduler.max-iteraciones:10000}")
    private int maxIteraciones;

    @Value("${app.scheduler.timeout-segundos:60}")
    private int timeoutSegundos;

    public ResultadoGeneracion ejecutar(String semestre, Integer horarioId, List<Integer> cursoIds) {
        long inicio = System.currentTimeMillis();

        // ── FASE 1: CARGA EN MEMORIA (única) ─────────────────────────────
        ParametroSemestre params = paramRepo.findBySemestre(semestre).orElseThrow(
            () -> new IllegalArgumentException("Sin parámetros para semestre: " + semestre)
        );
        List<Grupo> grupos = (cursoIds != null && !cursoIds.isEmpty())
            ? grupoRepo.findByEstadoAndCursoCursoIdIn("ACTIVO", cursoIds)
            : grupoRepo.findByEstado("ACTIVO");
        List<FranjaHoraria> franjas = franjaRepo.findByEsValidaTrue();
        List<Aula>          aulas   = aulaRepo.findByActivaTrue();

        Set<String> compatibilidades = compatRepo.findAll().stream()
            .map(c -> c.getDocente().getDocenteId() + "_" + c.getCurso().getCursoId())
            .collect(Collectors.toSet());

        HorarioContexto contexto = new HorarioContexto(params, compatibilidades);

        // ── FASE 2: ORDENAR GRUPOS POR PRIORIDAD ─────────────────────────
        grupos = ordenarPorPrioridad(grupos, aulas);

        // ── FASE 3: CICLO DE ASIGNACIÓN GREEDY ───────────────────────────
        List<AsignacionCandidato> asignaciones = new ArrayList<>();
        List<ConflictoAsignacion> conflictos   = new ArrayList<>();
        int iteraciones = 0;

        for (Grupo grupo : grupos) {
            int requeridas = grupo.getCurso().getFrecuenciaSemanal();
            int asignadas  = 0;

            while (asignadas < requeridas) {
                iteraciones++;
                if (iteraciones > maxIteraciones || tiempoExcedido(inicio)) {
                    conflictos.add(new ConflictoAsignacion(grupo, asignadas + 1, "TIMEOUT"));
                    break;
                }

                List<AsignacionCandidato> candidatos = construirCandidatos(grupo, franjas, aulas, contexto);
                if (candidatos.isEmpty()) {
                    String hcCausa = identificarHCCausa(grupo, franjas, aulas, contexto);
                    conflictos.add(new ConflictoAsignacion(grupo, asignadas + 1, hcCausa));
                    asignadas++;
                    continue;
                }

                AsignacionCandidato mejor = seleccionarMejor(candidatos, contexto);
                asignaciones.add(mejor);
                contexto.registrarAsignacion(mejor);
                asignadas++;
            }
        }

        // ── FASE 4: REPAIR PASS ───────────────────────────────────────────
        if (!conflictos.isEmpty()) {
            repararConflictos(conflictos, asignaciones, franjas, aulas, contexto, inicio);
        }

        long tiempoMs = System.currentTimeMillis() - inicio;
        return new ResultadoGeneracion(asignaciones, conflictos, tiempoMs, iteraciones);
    }

    // ── ORDENAMIENTO ──────────────────────────────────────────────────────

    private List<Grupo> ordenarPorPrioridad(List<Grupo> grupos, List<Aula> aulas) {
        Map<Integer, Integer> opciones = new HashMap<>();
        for (Grupo g : grupos) {
            int tipoId = g.getCurso().getTipoAulaRequerida().getIdTipoAula();
            long aulasCompatibles = aulas.stream()
                .filter(a -> a.getTipoAula().getIdTipoAula().equals(tipoId)
                          && a.getCapacidad() >= g.getNumInscritos())
                .count();
            int dispCount = g.getDocente().getDisponibilidades().size();
            opciones.put(g.getGrupoId(), (int)(aulasCompatibles * dispCount));
        }

        // Dentro del mismo bucket (numInscritos, semestreNivel), orden aleatorio
        // para que cada ejecución produzca distribución diferente
        List<Grupo> shuffled = new ArrayList<>(grupos);
        Collections.shuffle(shuffled);

        return shuffled.stream()
            .sorted(Comparator
                .comparingInt((Grupo g) -> g.getNumInscritos()).reversed()
                .thenComparingInt(g -> -g.getCurso().getSemestreNivel())
                .thenComparingInt(g -> opciones.getOrDefault(g.getGrupoId(), 0))
            )
            .collect(Collectors.toList());
    }

    // ── CONSTRUCCIÓN DE CANDIDATOS ────────────────────────────────────────

    private List<AsignacionCandidato> construirCandidatos(Grupo grupo,
                                                           List<FranjaHoraria> franjas,
                                                           List<Aula> aulas,
                                                           HorarioContexto ctx) {
        List<AsignacionCandidato> resultado = new ArrayList<>();
        for (FranjaHoraria franja : franjas) {
            for (Aula aula : aulas) {
                AsignacionCandidato c = new AsignacionCandidato(grupo, grupo.getDocente(), aula, franja);
                if (pasaTodosHC(c, ctx)) resultado.add(c);
            }
        }
        return resultado;
    }

    private boolean pasaTodosHC(AsignacionCandidato c, HorarioContexto ctx) {
        for (HCEvaluator hc : hcEvaluators) {
            if (!hc.evaluate(c, ctx)) return false;
        }
        return true;
    }

    // ── SELECCIÓN POR SOFT CONSTRAINTS ───────────────────────────────────

    private AsignacionCandidato seleccionarMejor(List<AsignacionCandidato> candidatos,
                                                  HorarioContexto ctx) {
        // Preferir días que el grupo aún no tenga asignados
        List<String> diasUsados = ctx.getFranjasGrupo(
                candidatos.get(0).getGrupo().getGrupoId())
            .stream()
            .map(FranjaHoraria::getDiaSemana)
            .distinct()
            .toList();

        List<AsignacionCandidato> enDiaNuevo = candidatos.stream()
            .filter(c -> !diasUsados.contains(c.getFranja().getDiaSemana()))
            .toList();

        List<AsignacionCandidato> pool = enDiaNuevo.isEmpty() ? candidatos : enDiaNuevo;

        // Calcular puntaje total de SCs para cada candidato
        int maxScore = pool.stream()
            .mapToInt(c -> softEvaluators.stream().mapToInt(sc -> sc.score(c, ctx)).sum())
            .max()
            .orElse(0);

        // Recoger todos los que tengan el máximo puntaje y elegir uno al azar
        List<AsignacionCandidato> mejores = pool.stream()
            .filter(c -> softEvaluators.stream().mapToInt(sc -> sc.score(c, ctx)).sum() == maxScore)
            .collect(Collectors.toList());

        return mejores.get(new Random().nextInt(mejores.size()));
    }

    // ── FASE 4: REPAIR PASS ───────────────────────────────────────────────

    private void repararConflictos(List<ConflictoAsignacion> conflictos,
                                   List<AsignacionCandidato> asignaciones,
                                   List<FranjaHoraria> franjas,
                                   List<Aula> aulas,
                                   HorarioContexto contexto,
                                   long inicio) {
        Iterator<ConflictoAsignacion> it = conflictos.iterator();
        while (it.hasNext()) {
            if (tiempoExcedido(inicio)) break;
            ConflictoAsignacion conflicto = it.next();
            if ("TIMEOUT".equals(conflicto.hcViolado())) continue;

            Grupo grupo    = conflicto.grupo();
            int requeridas = grupo.getCurso().getFrecuenciaSemanal();
            int asignadas  = contexto.sesionesAsignadas(grupo.getGrupoId());
            boolean completo = true;

            while (asignadas < requeridas && !tiempoExcedido(inicio)) {
                List<AsignacionCandidato> candidatos = construirCandidatos(grupo, franjas, aulas, contexto);
                if (!candidatos.isEmpty()) {
                    AsignacionCandidato mejor = seleccionarMejor(candidatos, contexto);
                    asignaciones.add(mejor);
                    contexto.registrarAsignacion(mejor);
                    asignadas++;
                } else {
                    if (intentarSwap(grupo, franjas, aulas, asignaciones, contexto)) {
                        // slot liberado — reintentar esta sesión
                    } else {
                        completo = false;
                        asignadas++;
                    }
                }
            }
            if (completo) it.remove();
        }
    }

    private boolean intentarSwap(Grupo grupo,
                                  List<FranjaHoraria> franjas,
                                  List<Aula> aulas,
                                  List<AsignacionCandidato> asignaciones,
                                  HorarioContexto contexto) {
        int docenteId = grupo.getDocente().getDocenteId();

        for (FranjaHoraria franja : franjas) {
            boolean disponible = grupo.getDocente().getDisponibilidades().stream()
                .anyMatch(d -> d.cubre(franja));
            if (!disponible) continue;

            if (!contexto.isDocenteOcupado(docenteId, franja.getFranjaId())) continue;

            AsignacionCandidato bloqueadora = contexto.getAsignacionEnFranja(docenteId, franja.getFranjaId());
            if (bloqueadora == null) continue;
            if (bloqueadora.getGrupo().getGrupoId().equals(grupo.getGrupoId())) continue;

            contexto.liberarAsignacion(bloqueadora);

            List<AsignacionCandidato> alternativas = construirCandidatos(
                bloqueadora.getGrupo(), franjas, aulas, contexto);

            if (!alternativas.isEmpty()) {
                AsignacionCandidato nuevaPos = seleccionarMejor(alternativas, contexto);
                contexto.registrarAsignacion(nuevaPos);
                asignaciones.removeIf(a -> a == bloqueadora);
                asignaciones.add(nuevaPos);
                return true;
            } else {
                contexto.registrarAsignacion(bloqueadora);
            }
        }
        return false;
    }

    // ── DIAGNÓSTICO DE CAUSA HC ───────────────────────────────────────────

    private String identificarHCCausa(Grupo grupo,
                                       List<FranjaHoraria> franjas,
                                       List<Aula> aulas,
                                       HorarioContexto ctx) {
        if (!ctx.isCompatible(grupo.getDocente().getDocenteId(), grupo.getCurso().getCursoId())) {
            return "HC-10";
        }
        Integer tipoRequerido = grupo.getCurso().getTipoAulaRequerida().getIdTipoAula();
        boolean hayAulasTipo = aulas.stream()
            .anyMatch(a -> a.getTipoAula().getIdTipoAula().equals(tipoRequerido));
        if (!hayAulasTipo) return "HC-04";

        boolean hayCapacidad = aulas.stream()
            .anyMatch(a -> a.getCapacidad() >= grupo.getNumInscritos());
        if (!hayCapacidad) return "HC-05";

        boolean hayFranjaDisponible = franjas.stream()
            .anyMatch(f -> grupo.getDocente().getDisponibilidades().stream()
                .anyMatch(d -> d.cubre(f)));
        if (!hayFranjaDisponible) return "HC-03";

        boolean docenteLibre = franjas.stream()
            .anyMatch(f -> !ctx.isDocenteOcupado(grupo.getDocente().getDocenteId(), f.getFranjaId()));
        if (!docenteLibre) return "HC-01";

        int semestre = grupo.getCurso().getSemestreNivel();
        boolean hayFranjaSinSemestre = franjas.stream()
            .anyMatch(f -> !ctx.isSemestreOcupado(semestre, f.getFranjaId()));
        if (!hayFranjaSinSemestre) return "HC-12";

        Set<String> diasUsados = ctx.getFranjasGrupo(grupo.getGrupoId()).stream()
            .map(FranjaHoraria::getDiaSemana)
            .collect(Collectors.toSet());
        boolean hayDiaNuevo = franjas.stream()
            .anyMatch(f -> !diasUsados.contains(f.getDiaSemana()));
        if (!hayDiaNuevo) return "HC-13";

        return "HC-02";
    }

    // ── UTILIDADES ────────────────────────────────────────────────────────

    private boolean tiempoExcedido(long inicio) {
        return (System.currentTimeMillis() - inicio) > ((long) timeoutSegundos * 1000);
    }
}
