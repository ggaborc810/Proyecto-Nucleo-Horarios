package co.edu.unbosque.horarios.dto;

import co.edu.unbosque.horarios.model.Asignacion;

public record AsignacionDTO(
    Integer idAsignacion,
    Integer grupoId,
    String seccionGrupo,
    String nombreCurso,
    String nombreDocente,
    Integer aulaId,
    String codigoAula,
    Integer franjaId,
    String diaSemana,
    String horaInicio,
    String horaFin,
    String estado,
    String hcViolado
) {
    public static AsignacionDTO from(Asignacion a) {
        return new AsignacionDTO(
            a.getIdAsignacion(),
            a.getGrupo().getGrupoId(),
            a.getGrupo().getSeccion(),
            a.getGrupo().getCurso().getNombreCurso(),
            a.getDocente() != null ? a.getDocente().getNombreCompleto() : null,
            a.getAula() != null ? a.getAula().getAulaId() : null,
            a.getAula() != null ? a.getAula().getCodigoAula() : null,
            a.getFranja() != null ? a.getFranja().getFranjaId() : null,
            a.getFranja() != null ? a.getFranja().getDiaSemana() : null,
            a.getFranja() != null ? a.getFranja().getHoraInicio().toString() : null,
            a.getFranja() != null ? a.getFranja().getHoraValida().toString() : null,
            a.getEstado(),
            a.getHcViolado()
        );
    }
}
