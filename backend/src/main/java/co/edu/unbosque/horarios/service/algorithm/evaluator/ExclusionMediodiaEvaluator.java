package co.edu.unbosque.horarios.service.algorithm.evaluator;

import co.edu.unbosque.horarios.model.FranjaHoraria;
import co.edu.unbosque.horarios.model.ParametroSemestre;
import co.edu.unbosque.horarios.service.algorithm.*;
import org.springframework.stereotype.Component;

/**
 * HC-07: Defensa en profundidad. La exclusión de mediodía ya debería estar cubierta
 * por es_valida=false en la franja, pero este evaluador verifica explícitamente
 * si los parámetros están disponibles en el contexto.
 */
@Component
public class ExclusionMediodiaEvaluator implements HCEvaluator {

    @Override
    public boolean evaluate(AsignacionCandidato c, HorarioContexto ctx) {
        ParametroSemestre p = ctx.getParametros();
        if (p == null) {
            // Sin parámetros en contexto: confiar en es_valida (HC-06)
            return Boolean.TRUE.equals(c.getFranja().getEsValida());
        }
        FranjaHoraria f = c.getFranja();
        // La sesión viola HC-07 si solapa con el rango de exclusión
        boolean solapa = f.getHoraInicio().isBefore(p.getExclusionFin())
                      && f.getHoraValida().isAfter(p.getExclusionInicio());
        return !solapa;
    }

    @Override
    public String getHCId() { return "HC-07"; }
}
