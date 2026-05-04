package co.edu.unbosque.horarios.algorithm;

import co.edu.unbosque.horarios.model.Grupo;
import co.edu.unbosque.horarios.service.algorithm.*;
import co.edu.unbosque.horarios.service.algorithm.evaluator.FrecuenciaSemanalEvaluator;
import org.junit.jupiter.api.Test;

import static co.edu.unbosque.horarios.testutil.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;

class HC09FrecuenciaSemanalTest {

    private final FrecuenciaSemanalEvaluator evaluador = new FrecuenciaSemanalEvaluator();

    @Test
    void grupoAunRequiereSesiones() {
        HorarioContexto ctx = new HorarioContexto();
        Grupo g = grupoConFrecuencia(1, 2); // requiere 2
        assertTrue(evaluador.evaluate(
            new AsignacionCandidato(g, null, null, franja(5, "LUNES", "07:00")), ctx));
    }

    @Test
    void grupoCumplioFrecuencia() {
        HorarioContexto ctx = new HorarioContexto();
        Grupo g = grupoConFrecuencia(1, 2);
        ctx.registrarAsignacion(new AsignacionCandidato(g, docente(1), aula(1), franja(5, "LUNES", "07:00")));
        ctx.registrarAsignacion(new AsignacionCandidato(g, docente(1), aula(1), franja(7, "MARTES", "07:00")));

        assertFalse(evaluador.evaluate(
            new AsignacionCandidato(g, null, null, franja(9, "MIERCOLES", "07:00")), ctx));
    }

    @Test
    void frecuencia1RequiereExactamente1Sesion() {
        HorarioContexto ctx = new HorarioContexto();
        Grupo g = grupoConFrecuencia(1, 1);
        assertTrue(evaluador.evaluate(
            new AsignacionCandidato(g, null, null, franja(5, "LUNES", "07:00")), ctx));

        ctx.registrarAsignacion(new AsignacionCandidato(g, docente(1), aula(1), franja(5, "LUNES", "07:00")));
        assertFalse(evaluador.evaluate(
            new AsignacionCandidato(g, null, null, franja(7, "MARTES", "07:00")), ctx));
    }

    @Test
    void getHCId() {
        assertEquals("HC-09", evaluador.getHCId());
    }
}
