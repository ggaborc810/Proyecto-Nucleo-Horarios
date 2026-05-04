package co.edu.unbosque.horarios.service.algorithm.evaluator;

import co.edu.unbosque.horarios.service.algorithm.*;
import org.springframework.stereotype.Component;

@Component
public class SinTraslapeAulaEvaluator implements HCEvaluator {

    @Override
    public boolean evaluate(AsignacionCandidato c, HorarioContexto ctx) {
        return !ctx.isAulaOcupada(
            c.getAula().getAulaId(),
            c.getFranja().getFranjaId()
        );
    }

    @Override
    public String getHCId() { return "HC-02"; }
}
