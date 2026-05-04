package co.edu.unbosque.horarios.algorithm;

import co.edu.unbosque.horarios.model.*;
import co.edu.unbosque.horarios.service.algorithm.*;
import co.edu.unbosque.horarios.service.algorithm.evaluator.DisponibilidadDocenteEvaluator;
import org.junit.jupiter.api.Test;

import static co.edu.unbosque.horarios.testutil.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;

class HC03DisponibilidadDocenteTest {

    private final DisponibilidadDocenteEvaluator evaluador = new DisponibilidadDocenteEvaluator();

    @Test
    void franjaDentroDeDisponibilidad() {
        Docente d = docenteConDisponibilidad("LUNES", "07:00", "13:00");
        FranjaHoraria f = franja(5, "LUNES", "07:00");  // 07:00-09:00
        assertTrue(evaluador.evaluate(
            new AsignacionCandidato(null, d, null, f), new HorarioContexto()));
    }

    @Test
    void franjaFueraDeDisponibilidad() {
        Docente d = docenteConDisponibilidad("LUNES", "07:00", "09:00");
        FranjaHoraria f = franja(6, "LUNES", "09:00");  // 09:00-11:00 — no cabe en 07-09
        assertFalse(evaluador.evaluate(
            new AsignacionCandidato(null, d, null, f), new HorarioContexto()));
    }

    @Test
    void franjaDiaDiferente() {
        Docente d = docenteConDisponibilidad("LUNES", "07:00", "13:00");
        FranjaHoraria f = franja(10, "MARTES", "07:00");
        assertFalse(evaluador.evaluate(
            new AsignacionCandidato(null, d, null, f), new HorarioContexto()));
    }

    @Test
    void docenteSinDisponibilidad() {
        Docente d = docente(1); // sin disponibilidades
        assertFalse(evaluador.evaluate(
            new AsignacionCandidato(null, d, null, franja(5, "LUNES", "07:00")), new HorarioContexto()));
    }

    @Test
    void getHCId() {
        assertEquals("HC-03", evaluador.getHCId());
    }
}
