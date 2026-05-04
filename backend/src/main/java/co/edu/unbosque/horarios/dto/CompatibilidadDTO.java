package co.edu.unbosque.horarios.dto;

import co.edu.unbosque.horarios.model.CompatibilidadDocenteCurso;

public record CompatibilidadDTO(
    Integer compatibilidadId,
    Integer docenteId,
    Integer cursoId,
    String codigoCurso,
    String nombreCurso
) {
    public static CompatibilidadDTO from(CompatibilidadDocenteCurso c) {
        return new CompatibilidadDTO(
            c.getCompatibilidadId(),
            c.getDocente().getDocenteId(),
            c.getCurso().getCursoId(),
            c.getCurso().getCodigoCurso(),
            c.getCurso().getNombreCurso()
        );
    }
}
