package co.edu.unbosque.horarios.service.algorithm.evaluator;

import co.edu.unbosque.horarios.service.algorithm.*;
import org.springframework.stereotype.Component;

@Component
public class CapacidadAulaEvaluator implements HCEvaluator {

    @Override
    public boolean evaluate(AsignacionCandidato c, HorarioContexto ctx) {
        return c.getAula().getCapacidad() >= c.getGrupo().getNumInscritos();
    }

    @Override
    public String getHCId() { return "HC-05"; }
}
