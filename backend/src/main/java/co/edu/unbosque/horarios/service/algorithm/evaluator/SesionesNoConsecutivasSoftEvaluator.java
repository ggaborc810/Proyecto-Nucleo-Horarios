package co.edu.unbosque.horarios.service.algorithm.evaluator;

import co.edu.unbosque.horarios.model.FranjaHoraria;
import co.edu.unbosque.horarios.service.algorithm.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SesionesNoConsecutivasSoftEvaluator implements SoftEvaluator {

    private static final int PUNTAJE_BASE              = 100;
    private static final int PENALIZACION_DIA_ADYACENTE = 50;

    @Override
    public int score(AsignacionCandidato c, HorarioContexto ctx) {
        List<FranjaHoraria> ya = ctx.getFranjasGrupo(c.getGrupo().getGrupoId());
        if (ya.isEmpty()) return PUNTAJE_BASE;

        int diaCandidato = ordinalDia(c.getFranja().getDiaSemana());
        int penalizacion = 0;

        for (FranjaHoraria f : ya) {
            int distancia = Math.abs(diaCandidato - ordinalDia(f.getDiaSemana()));
            if (distancia == 0) penalizacion += PENALIZACION_DIA_ADYACENTE * 2;
            else if (distancia == 1) penalizacion += PENALIZACION_DIA_ADYACENTE;
        }
        return PUNTAJE_BASE - penalizacion;
    }

    @Override
    public String getSCId() { return "SC-01"; }

    private int ordinalDia(String dia) {
        return switch (dia) {
            case "LUNES"     -> 1;
            case "MARTES"    -> 2;
            case "MIERCOLES" -> 3;
            case "JUEVES"    -> 4;
            case "VIERNES"   -> 5;
            case "SABADO"    -> 6;
            default          -> -1;
        };
    }
}
