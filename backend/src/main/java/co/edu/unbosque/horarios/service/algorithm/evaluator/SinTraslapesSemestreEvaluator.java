package co.edu.unbosque.horarios.service.algorithm.evaluator;

import co.edu.unbosque.horarios.service.algorithm.*;
import org.springframework.stereotype.Component;

/**
 * HC-12: Dos grupos del mismo semestre no pueden compartir la misma franja.
 * Los estudiantes de un semestre asisten a todas las materias de ese semestre,
 * por lo que cualquier traslape hace imposible el horario para ellos.
 */
@Component
public class SinTraslapesSemestreEvaluator implements HCEvaluator {

    @Override
    public boolean evaluate(AsignacionCandidato c, HorarioContexto ctx) {
        int semestre = c.getGrupo().getCurso().getSemestreNivel();
        return !ctx.isSemestreOcupado(semestre, c.getFranja().getFranjaId());
    }

    @Override
    public String getHCId() { return "HC-12"; }
}
