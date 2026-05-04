package co.edu.unbosque.horarios.service.algorithm.evaluator;

import co.edu.unbosque.horarios.service.algorithm.*;
import org.springframework.stereotype.Component;

@Component
public class TipoAulaCompatibleEvaluator implements HCEvaluator {

    @Override
    public boolean evaluate(AsignacionCandidato c, HorarioContexto ctx) {
        Integer tipoCurso = c.getGrupo().getCurso().getTipoAulaRequerida().getIdTipoAula();
        Integer tipoAula  = c.getAula().getTipoAula().getIdTipoAula();
        return tipoCurso.equals(tipoAula);
    }

    @Override
    public String getHCId() { return "HC-04"; }
}
