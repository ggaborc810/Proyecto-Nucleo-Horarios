package co.edu.unbosque.horarios.dto;

import co.edu.unbosque.horarios.model.Docente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record DocenteDTO(
    Integer docenteId,
    @NotBlank String numeroDocumento,
    @NotBlank String nombreCompleto,
    @NotBlank String tipoVinculacion,
    @Positive Integer horasMaxSemana,
    @Email @NotBlank String email,
    Integer totalDisponibilidades,
    Integer totalCompatibilidades
) {
    public static DocenteDTO from(Docente d, int totalCompatibilidades) {
        return new DocenteDTO(
            d.getDocenteId(), d.getNumeroDocumento(), d.getNombreCompleto(),
            d.getTipoVinculacion(), d.getHorasMaxSemana(), d.getEmail(),
            d.getDisponibilidades().size(), totalCompatibilidades
        );
    }

    public static DocenteDTO from(Docente d) {
        return from(d, 0);
    }
}
