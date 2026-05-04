package co.edu.unbosque.horarios.algorithm;

import co.edu.unbosque.horarios.model.FranjaHoraria;
import co.edu.unbosque.horarios.service.algorithm.*;
import co.edu.unbosque.horarios.service.algorithm.evaluator.DuracionFijaEvaluator;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static co.edu.unbosque.horarios.testutil.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;

class HC08DuracionFijaTest {

    private final DuracionFijaEvaluator evaluador = new DuracionFijaEvaluator();

    @Test
    void duracionDosHoras() {
        assertTrue(evaluador.evaluate(
            new AsignacionCandidato(null, null, null, franja(5, "LUNES", "07:00")),
            new HorarioContexto()));
    }

    @Test
    void duracionIncorrectaUnaHora() {
        FranjaHoraria f = franja(5, "LUNES", "07:00");
        f.setHoraValida(LocalTime.of(8, 0)); // solo 1 hora
        assertFalse(evaluador.evaluate(
            new AsignacionCandidato(null, null, null, f), new HorarioContexto()));
    }

    @Test
    void duracionIncorrectaTresHoras() {
        FranjaHoraria f = franja(5, "LUNES", "07:00");
        f.setHoraValida(LocalTime.of(10, 0)); // 3 horas
        assertFalse(evaluador.evaluate(
            new AsignacionCandidato(null, null, null, f), new HorarioContexto()));
    }

    @Test
    void getHCId() {
        assertEquals("HC-08", evaluador.getHCId());
    }
}
