package co.edu.unbosque.horarios.service.algorithm;

import co.edu.unbosque.horarios.model.FranjaHoraria;
import co.edu.unbosque.horarios.model.ParametroSemestre;

import java.util.*;

/**
 * Estado mutable en memoria del ciclo de asignación.
 * Cero queries después de la carga inicial.
 */
public class HorarioContexto {

    // HC-01: docente ocupado en franja
    private final Set<String> docenteFranjaOcupada = new HashSet<>();
    // HC-02: aula ocupada en franja
    private final Set<String> aulaFranjaOcupada    = new HashSet<>();
    // HC-12: semestre ocupado en franja
    private final Set<String> semestreFranjaOcupada = new HashSet<>();
    // HC-13: bloque horario (horaInicio) ya usado por un grupo en cualquier día
    private final Map<Integer, Set<String>> diasGrupo = new HashMap<>();

    private final Map<Integer, Integer>             sesionesPorGrupo  = new HashMap<>();
    private final Map<Integer, List<FranjaHoraria>> franjasGrupo      = new HashMap<>();
    private final Map<Integer, List<FranjaHoraria>> franjasDocente    = new HashMap<>();

    // SC-03: cuántos grupos globalmente usan cada franja específica (día+hora)
    private final Map<Integer, Integer> sesionesPorFranja      = new HashMap<>();
    // SC-04: cuántos grupos de cada semestre están en cada día
    private final Map<String,  Integer> sesionesPorSemestreDia = new HashMap<>();

    // Para repair pass: docenteId_franjaId → candidato asignado
    private final Map<String, AsignacionCandidato> asignacionPorDocenteFranja = new HashMap<>();

    private final ParametroSemestre parametros;
    private final Set<String>        compatibilidades;

    public HorarioContexto() {
        this.parametros       = null;
        this.compatibilidades = null;
    }

    public HorarioContexto(ParametroSemestre parametros, Set<String> compatibilidades) {
        this.parametros       = parametros;
        this.compatibilidades = compatibilidades;
    }

    // ── Registro / liberación ─────────────────────────────────────────────

    public void registrarAsignacion(AsignacionCandidato c) {
        int docenteId = c.getDocente().getDocenteId();
        int franjaId  = c.getFranja().getFranjaId();
        int grupoId   = c.getGrupo().getGrupoId();
        int semestre  = c.getGrupo().getCurso().getSemestreNivel();
        String dia    = c.getFranja().getDiaSemana();

        docenteFranjaOcupada.add(docenteId + "_" + franjaId);
        if (c.getAula() != null) {
            aulaFranjaOcupada.add(c.getAula().getAulaId() + "_" + franjaId);
        }
        semestreFranjaOcupada.add(semestre + "_" + franjaId);
        diasGrupo.computeIfAbsent(grupoId, k -> new HashSet<>()).add(dia);

        sesionesPorGrupo.merge(grupoId, 1, Integer::sum);
        sesionesPorFranja.merge(franjaId, 1, Integer::sum);
        sesionesPorSemestreDia.merge(semestre + "_" + dia, 1, Integer::sum);

        franjasGrupo.computeIfAbsent(grupoId,   k -> new ArrayList<>()).add(c.getFranja());
        franjasDocente.computeIfAbsent(docenteId, k -> new ArrayList<>()).add(c.getFranja());
        asignacionPorDocenteFranja.put(docenteId + "_" + franjaId, c);
    }

    public void liberarAsignacion(AsignacionCandidato c) {
        int docenteId = c.getDocente().getDocenteId();
        int franjaId  = c.getFranja().getFranjaId();
        int grupoId   = c.getGrupo().getGrupoId();
        int semestre  = c.getGrupo().getCurso().getSemestreNivel();
        String dia    = c.getFranja().getDiaSemana();

        docenteFranjaOcupada.remove(docenteId + "_" + franjaId);
        if (c.getAula() != null) {
            aulaFranjaOcupada.remove(c.getAula().getAulaId() + "_" + franjaId);
        }
        semestreFranjaOcupada.remove(semestre + "_" + franjaId);

        Set<String> dias = diasGrupo.get(grupoId);
        if (dias != null) {
            dias.remove(dia);
            if (dias.isEmpty()) diasGrupo.remove(grupoId);
        }

        decrementar(sesionesPorGrupo, grupoId);
        decrementar(sesionesPorFranja, franjaId);
        decrementar(sesionesPorSemestreDia, semestre + "_" + dia);

        List<FranjaHoraria> fG = franjasGrupo.get(grupoId);
        if (fG != null) fG.remove(c.getFranja());
        List<FranjaHoraria> fD = franjasDocente.get(docenteId);
        if (fD != null) fD.remove(c.getFranja());

        asignacionPorDocenteFranja.remove(docenteId + "_" + franjaId);
    }

    private <K> void decrementar(Map<K, Integer> mapa, K clave) {
        int v = mapa.getOrDefault(clave, 0);
        if (v <= 1) mapa.remove(clave);
        else mapa.put(clave, v - 1);
    }

    // ── Consultas ─────────────────────────────────────────────────────────

    public AsignacionCandidato getAsignacionEnFranja(int docenteId, int franjaId) {
        return asignacionPorDocenteFranja.get(docenteId + "_" + franjaId);
    }

    public boolean isDocenteOcupado(int docenteId, int franjaId) {
        return docenteFranjaOcupada.contains(docenteId + "_" + franjaId);
    }

    public boolean isAulaOcupada(int aulaId, int franjaId) {
        return aulaFranjaOcupada.contains(aulaId + "_" + franjaId);
    }

    /** HC-12: ¿Ya hay un grupo del mismo semestre en esta franja (día+hora)? */
    public boolean isSemestreOcupado(int semestre, int franjaId) {
        return semestreFranjaOcupada.contains(semestre + "_" + franjaId);
    }

    /**
     * HC-13: ¿El grupo ya tiene una sesión que empieza a esta hora en cualquier día?
     * Evita que un grupo quede, p.ej., Lunes 07-09 Y Martes 07-09.
     */
    public boolean grupoUsaDia(int grupoId, String diaSemana) {
        Set<String> dias = diasGrupo.get(grupoId);
        return dias != null && dias.contains(diaSemana);
    }

    public int sesionesAsignadas(int grupoId) {
        return sesionesPorGrupo.getOrDefault(grupoId, 0);
    }

    /** SC-03: grupos que globalmente usan esta franja específica (día+hora). */
    public int sesionesEnFranja(int franjaId) {
        return sesionesPorFranja.getOrDefault(franjaId, 0);
    }

    /** SC-04: sesiones del semestre dado en ese día. */
    public int sesionesEnSemestreDia(int semestre, String dia) {
        return sesionesPorSemestreDia.getOrDefault(semestre + "_" + dia, 0);
    }

    public List<FranjaHoraria> getFranjasGrupo(int grupoId) {
        return franjasGrupo.getOrDefault(grupoId, List.of());
    }

    public List<FranjaHoraria> getFranjasDocente(int docenteId) {
        return franjasDocente.getOrDefault(docenteId, List.of());
    }

    public ParametroSemestre getParametros() { return parametros; }

    public boolean isCompatible(int docenteId, int cursoId) {
        return compatibilidades != null && compatibilidades.contains(docenteId + "_" + cursoId);
    }

    public boolean hasCompatibilidades() { return compatibilidades != null; }
}
