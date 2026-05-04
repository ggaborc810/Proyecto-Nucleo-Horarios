package co.edu.unbosque.horarios.service.algorithm.evaluator;

import co.edu.unbosque.horarios.service.algorithm.*;
import org.springframework.stereotype.Component;

@Component
public class DisponibilidadDocenteEvaluator implements HCEvaluator {

    @Override
    public boolean evaluate(AsignacionCandidato c, HorarioContexto ctx) {
        return c.getDocente().getDisponibilidades().stream()
            .anyMatch(d -> d.cubre(c.getFranja()));
    }

    @Override
    public String getHCId() { return "HC-03"; }
}
