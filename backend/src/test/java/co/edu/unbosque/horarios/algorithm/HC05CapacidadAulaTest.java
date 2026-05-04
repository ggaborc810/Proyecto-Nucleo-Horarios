package co.edu.unbosque.horarios.algorithm;

import co.edu.unbosque.horarios.service.algorithm.*;
import co.edu.unbosque.horarios.service.algorithm.evaluator.CapacidadAulaEvaluator;
import org.junit.jupiter.api.Test;

import static co.edu.unbosque.horarios.testutil.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;

class HC05CapacidadAulaTest {

    private final CapacidadAulaEvaluator evaluador = new CapacidadAulaEvaluator();

    @Test
    void capacidadSuficiente() {
        assertTrue(evaluador.evaluate(
            new AsignacionCandidato(grupo(1, 28), null, aula(1, 35), null),
            new HorarioContexto()));
    }

    @Test
    void capacidadInsuficiente() {
        assertFalse(evaluador.evaluate(
            new AsignacionCandidato(grupo(1, 36), null, aula(1, 35), null),
            new HorarioContexto()));
    }

    @Test
    void capacidadExacta() {
        assertTrue(evaluador.evaluate(
            new AsignacionCandidato(grupo(1, 35), null, aula(1, 35), null),
            new HorarioContexto()));
    }

    @Test
    void getHCId() {
        assertEquals("HC-05", evaluador.getHCId());
    }
}
