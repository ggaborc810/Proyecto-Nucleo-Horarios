package co.edu.unbosque.horarios.service.algorithm.evaluator;

import co.edu.unbosque.horarios.service.algorithm.*;
import org.springframework.stereotype.Component;

@Component
public class FranjaHorariaPermitidaEvaluator implements HCEvaluator {

    @Override
    public boolean evaluate(AsignacionCandidato c, HorarioContexto ctx) {
        return Boolean.TRUE.equals(c.getFranja().getEsValida());
    }

    @Override
    public String getHCId() { return "HC-06"; }
}
