package co.edu.unbosque.horarios.service.algorithm.evaluator;

import co.edu.unbosque.horarios.service.algorithm.*;
import org.springframework.stereotype.Component;

/**
 * SC-04: Evitar concentrar las clases de un semestre en un mismo día.
 * Penaliza asignar a un día cuando ese semestre ya tiene muchas sesiones ese día.
 */
@Component
public class DistribucionSemestreDiaSoftEvaluator implements SoftEvaluator {

    private static final int PUNTAJE_BASE         = 100;
    private static final int PENALIZACION_POR_SESION = 20;

    @Override
    public int score(AsignacionCandidato c, HorarioContexto ctx) {
        int semestre = c.getGrupo().getCurso().getSemestreNivel();
        String dia   = c.getFranja().getDiaSemana();
        int ya = ctx.sesionesEnSemestreDia(semestre, dia);
        return Math.max(0, PUNTAJE_BASE - ya * PENALIZACION_POR_SESION);
    }

    @Override
    public String getSCId() { return "SC-04"; }
}
