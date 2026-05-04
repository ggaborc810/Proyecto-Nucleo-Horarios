package co.edu.unbosque.horarios.service.algorithm.evaluator;

import co.edu.unbosque.horarios.service.algorithm.*;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class FranjaEnRangoCursoEvaluator implements HCEvaluator {

    @Override
    public boolean evaluate(AsignacionCandidato c, HorarioContexto ctx) {
        LocalTime inicio  = c.getFranja().getHoraInicio();
        LocalTime fin     = c.getFranja().getHoraValida();
        LocalTime permIni = c.getGrupo().getCurso().getHoraInicioPerm();
        LocalTime permFin = c.getGrupo().getCurso().getHoraFinPerm();
        return !inicio.isBefore(permIni) && !fin.isAfter(permFin);
    }

    @Override
    public String getHCId() { return "HC-11"; }
}
