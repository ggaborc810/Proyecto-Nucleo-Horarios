package co.edu.unbosque.horarios.dto;

import co.edu.unbosque.horarios.model.DisponibilidadDocente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record DisponibilidadDTO(
    Integer disponibilidadId,
    Integer docenteId,
    @NotBlank String diaSemana,
    @NotNull LocalTime horaInicio,
    @NotNull LocalTime horaFin
) {
    public static DisponibilidadDTO from(DisponibilidadDocente d) {
        return new DisponibilidadDTO(
            d.getDisponibilidadId(),
            d.getDocente().getDocenteId(),
            d.getDiaSemana(),
            d.getHoraInicio(),
            d.getHoraFin()
        );
    }
}
