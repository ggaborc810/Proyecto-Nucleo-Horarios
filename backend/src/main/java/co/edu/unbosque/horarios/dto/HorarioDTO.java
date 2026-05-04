package co.edu.unbosque.horarios.dto;

import co.edu.unbosque.horarios.model.Horario;

import java.time.LocalDateTime;
import java.util.List;

public record HorarioDTO(
    Integer horarioId,
    String semestre,
    String estado,
    LocalDateTime fechaGeneracion,
    LocalDateTime fechaPublicacion,
    int totalConflictos,
    List<AsignacionDTO> asignaciones,
    List<FranjaDTO> franjas
) {
    public static HorarioDTO from(Horario h, List<AsignacionDTO> asignaciones) {
        int conflictos = (int) asignaciones.stream().filter(a -> a.hcViolado() != null).count();
        return new HorarioDTO(
            h.getHorarioId(), h.getSemestre(), h.getEstado(),
            h.getFechaGeneracion(), h.getFechaPublicacion(),
            conflictos, asignaciones, List.of()
        );
    }

    public static HorarioDTO from(Horario h, List<AsignacionDTO> asignaciones, List<FranjaDTO> franjas) {
        int conflictos = (int) asignaciones.stream().filter(a -> a.hcViolado() != null).count();
        return new HorarioDTO(
            h.getHorarioId(), h.getSemestre(), h.getEstado(),
            h.getFechaGeneracion(), h.getFechaPublicacion(),
            conflictos, asignaciones, franjas
        );
    }
}
