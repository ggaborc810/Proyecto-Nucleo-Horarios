package co.edu.unbosque.horarios.service.algorithm.evaluator;

import co.edu.unbosque.horarios.service.algorithm.*;
import org.springframework.stereotype.Component;

@Component
public class FrecuenciaSemanalEvaluator implements HCEvaluator {

    @Override
    public boolean evaluate(AsignacionCandidato c, HorarioContexto ctx) {
        int yaAsignadas = ctx.sesionesAsignadas(c.getGrupo().getGrupoId());
        int requeridas  = c.getGrupo().getCurso().getFrecuenciaSemanal();
        return yaAsignadas < requeridas;
    }

    @Override
    public String getHCId() { return "HC-09"; }
}
