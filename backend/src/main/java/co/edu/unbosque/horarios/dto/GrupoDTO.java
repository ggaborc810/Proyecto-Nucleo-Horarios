package co.edu.unbosque.horarios.dto;

import co.edu.unbosque.horarios.model.Grupo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record GrupoDTO(
    Integer grupoId,
    @NotBlank String seccion,
    Integer numInscritos,
    String estado,
    LocalDate fechaCierre,
    String causaCierre,
    @NotNull Integer cursoId,
    String nombreCurso,
    @NotNull Integer docenteId,
    String nombreDocente
) {
    public static GrupoDTO from(Grupo g) {
        return new GrupoDTO(
            g.getGrupoId(), g.getSeccion(), g.getNumInscritos(),
            g.getEstado(), g.getFechaCierre(), g.getCausaCierre(),
            g.getCurso().getCursoId(), g.getCurso().getNombreCurso(),
            g.getDocente().getDocenteId(), g.getDocente().getNombreCompleto()
        );
    }
}
