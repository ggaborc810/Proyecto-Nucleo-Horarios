package co.edu.unbosque.horarios.algorithm;

import co.edu.unbosque.horarios.service.algorithm.*;
import co.edu.unbosque.horarios.service.algorithm.evaluator.FranjaHorariaPermitidaEvaluator;
import org.junit.jupiter.api.Test;

import static co.edu.unbosque.horarios.testutil.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;

class HC06FranjaValidaTest {

    private final FranjaHorariaPermitidaEvaluator evaluador = new FranjaHorariaPermitidaEvaluator();

    @Test
    void franjaValida() {
        assertTrue(evaluador.evaluate(
            new AsignacionCandidato(null, null, null, franja(5, "LUNES", "07:00")),
            new HorarioContexto()));
    }

    @Test
    void franjaNoValida() {
        assertFalse(evaluador.evaluate(
            new AsignacionCandidato(null, null, null, franjaInvalida(5, "LUNES", "12:00")),
            new HorarioContexto()));
    }

    @Test
    void getHCId() {
        assertEquals("HC-06", evaluador.getHCId());
    }
}
