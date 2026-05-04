package co.edu.unbosque.horarios.service.algorithm.evaluator;

import co.edu.unbosque.horarios.service.algorithm.*;
import org.springframework.stereotype.Component;

/**
 * SC-03: Preferir franjas horarias globalmente menos usadas.
 * Evita que todas las materias queden en los mismos bloques horarios.
 */
@Component
public class DistribucionFranjasSoftEvaluator implements SoftEvaluator {

    private static final int PUNTAJE_BASE              = 100;
    // Penalización alta: cada grupo extra en la misma franja descuenta 20 puntos.
    // Con ~5 semestres activos concurrentes el score llega a 0, forzando variedad.
    private static final int PENALIZACION_POR_USO      = 20;

    @Override
    public int score(AsignacionCandidato c, HorarioContexto ctx) {
        int usos = ctx.sesionesEnFranja(c.getFranja().getFranjaId());
        return Math.max(0, PUNTAJE_BASE - usos * PENALIZACION_POR_USO);
    }

    @Override
    public String getSCId() { return "SC-03"; }
}
