package co.edu.unbosque.horarios.service.algorithm.evaluator;

import co.edu.unbosque.horarios.model.FranjaHoraria;
import co.edu.unbosque.horarios.service.algorithm.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DistribucionCargaSoftEvaluator implements SoftEvaluator {

    private static final int PUNTAJE_BASE                       = 100;
    private static final int PENALIZACION_POR_SESION_MISMO_DIA = 25;

    @Override
    public int score(AsignacionCandidato c, HorarioContexto ctx) {
        List<FranjaHoraria> ya = ctx.getFranjasDocente(c.getDocente().getDocenteId());
        if (ya.isEmpty()) return PUNTAJE_BASE;

        String diaCandidato = c.getFranja().getDiaSemana();
        long sesionesMismoDia = ya.stream()
            .filter(f -> f.getDiaSemana().equals(diaCandidato))
            .count();

        return PUNTAJE_BASE - (int) (sesionesMismoDia * PENALIZACION_POR_SESION_MISMO_DIA);
    }

    @Override
    public String getSCId() { return "SC-02"; }
}
