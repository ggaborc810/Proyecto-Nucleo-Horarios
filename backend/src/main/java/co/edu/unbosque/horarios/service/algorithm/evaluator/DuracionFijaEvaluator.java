package co.edu.unbosque.horarios.service.algorithm.evaluator;

import co.edu.unbosque.horarios.service.algorithm.*;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class DuracionFijaEvaluator implements HCEvaluator {

    @Override
    public boolean evaluate(AsignacionCandidato c, HorarioContexto ctx) {
        Duration d = Duration.between(
            c.getFranja().getHoraInicio(),
            c.getFranja().getHoraValida()
        );
        return d.toHours() == 2;
    }

    @Override
    public String getHCId() { return "HC-08"; }
}
