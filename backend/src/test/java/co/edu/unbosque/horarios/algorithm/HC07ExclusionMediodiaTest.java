package co.edu.unbosque.horarios.algorithm;

import co.edu.unbosque.horarios.model.ParametroSemestre;
import co.edu.unbosque.horarios.service.algorithm.*;
import co.edu.unbosque.horarios.service.algorithm.evaluator.ExclusionMediodiaEvaluator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static co.edu.unbosque.horarios.testutil.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;

class HC07ExclusionMediodiaTest {

    private final ExclusionMediodiaEvaluator evaluador = new ExclusionMediodiaEvaluator();

    private HorarioContexto ctxConParams() {
        return new HorarioContexto(parametros(), Set.of()); // exclusion 12:00-13:00
    }

    @Test
    void franjaMatutinaNoSolapa() {
        // 07:00-09:00 no solapa con 12:00-13:00
        assertTrue(evaluador.evaluate(
            new AsignacionCandidato(null, null, null, franja(5, "LUNES", "07:00")),
            ctxConParams()));
    }

    @Test
    void franjaMediodiaSolapa() {
        // 12:00-14:00 solapa con exclusion 12:00-13:00
        assertTrue(evaluador.evaluate(
            new AsignacionCandidato(null, null, null, franjaInvalida(20, "LUNES", "12:00")),
            ctxConParams()) == false);
    }

    @Test
    void franjaVespertinaNoSolapa() {
        // 13:00-15:00 no solapa con 12:00-13:00 (exactamente al borde)
        assertTrue(evaluador.evaluate(
            new AsignacionCandidato(null, null, null, franja(22, "LUNES", "13:00")),
            ctxConParams()));
    }

    @Test
    void sinParametrosFallbackAEsValida() {
        // Sin parámetros en contexto, confía en es_valida
        HorarioContexto ctx = new HorarioContexto();
        assertTrue(evaluador.evaluate(
            new AsignacionCandidato(null, null, null, franja(5, "LUNES", "07:00")), ctx));
        assertFalse(evaluador.evaluate(
            new AsignacionCandidato(null, null, null, franjaInvalida(20, "LUNES", "12:00")), ctx));
    }

    @Test
    void getHCId() {
        assertEquals("HC-07", evaluador.getHCId());
    }
}
