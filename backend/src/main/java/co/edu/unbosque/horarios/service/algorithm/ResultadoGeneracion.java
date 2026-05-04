package co.edu.unbosque.horarios.service.algorithm;

import java.util.List;

/** Resultado inmutable devuelto por SchedulerEngine tras el ciclo de asignación. */
public record ResultadoGeneracion(
    List<AsignacionCandidato> asignacionesExitosas,
    List<ConflictoAsignacion> conflictos,
    long tiempoEjecucionMs,
    int totalIteraciones
) {
    public boolean esCompleto() { return conflictos.isEmpty(); }
}
