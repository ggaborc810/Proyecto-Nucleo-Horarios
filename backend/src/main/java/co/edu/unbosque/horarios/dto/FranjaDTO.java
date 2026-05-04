package co.edu.unbosque.horarios.dto;

import co.edu.unbosque.horarios.model.FranjaHoraria;

public record FranjaDTO(
    Integer franjaId,
    String diaSemana,
    String horaInicio,
    String horaFin,
    Boolean esValida
) {
    public static FranjaDTO from(FranjaHoraria f) {
        return new FranjaDTO(
            f.getFranjaId(),
            f.getDiaSemana(),
            f.getHoraInicio().toString(),
            f.getHoraValida().toString(),
            f.getEsValida()
        );
    }
}
