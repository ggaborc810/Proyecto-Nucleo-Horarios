package co.edu.unbosque.horarios.dto;

import java.util.List;

public record ResultadoGeneracionDTO(
    Integer horarioId,
    String semestre,
    int totalAsignadas,
    int totalConflictos,
    long tiempoEjecucionMs,
    List<ConflictoDTO> conflictos
) {}
