package co.edu.unbosque.horarios.service.algorithm.evaluator;

import co.edu.unbosque.horarios.service.algorithm.*;
import org.springframework.stereotype.Component;

/**
 * HC-13: las sesiones semanales de un grupo/materia deben estar en dias distintos.
 */
@Component
public class SinRepeticionBloqueGrupoEvaluator implements HCEvaluator {

    @Override
    public boolean evaluate(AsignacionCandidato c, HorarioContexto ctx) {
        return !ctx.grupoUsaDia(
            c.getGrupo().getGrupoId(),
            c.getFranja().getDiaSemana()
        );
    }

    @Override
    public String getHCId() { return "HC-13"; }
}
