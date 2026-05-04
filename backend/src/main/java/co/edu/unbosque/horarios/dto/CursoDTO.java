package co.edu.unbosque.horarios.dto;

import co.edu.unbosque.horarios.model.Curso;
import jakarta.validation.constraints.*;

import java.time.LocalTime;

public record CursoDTO(
    Integer cursoId,
    @NotBlank String codigoCurso,
    @NotBlank String nombreCurso,
    @Min(1) @Max(4) Integer frecuenciaSemanal,
    @Min(1) @Max(10) Integer semestreNivel,
    @NotNull Integer tipoAulaId,
    String nombreTipoAula,
    LocalTime horaInicioPerm,
    LocalTime horaFinPerm
) {
    public static CursoDTO from(Curso c) {
        return new CursoDTO(
            c.getCursoId(), c.getCodigoCurso(), c.getNombreCurso(),
            c.getFrecuenciaSemanal(), c.getSemestreNivel(),
            c.getTipoAulaRequerida().getIdTipoAula(),
            c.getTipoAulaRequerida().getNombreTipo(),
            c.getHoraInicioPerm(),
            c.getHoraFinPerm()
        );
    }
}
