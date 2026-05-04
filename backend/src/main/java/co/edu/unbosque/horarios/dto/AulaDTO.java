package co.edu.unbosque.horarios.dto;

import co.edu.unbosque.horarios.model.Aula;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record AulaDTO(
    Integer aulaId,
    @NotBlank String codigoAula,
    @Positive Integer capacidad,
    String ubicacion,
    Boolean activa,
    Integer tipoAulaId,
    String nombreTipoAula
) {
    public static AulaDTO from(Aula a) {
        return new AulaDTO(
            a.getAulaId(), a.getCodigoAula(), a.getCapacidad(),
            a.getUbicacion(), a.getActiva(),
            a.getTipoAula().getIdTipoAula(), a.getTipoAula().getNombreTipo()
        );
    }
}
