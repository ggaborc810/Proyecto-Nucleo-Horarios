package co.edu.unbosque.horarios.algorithm;

import co.edu.unbosque.horarios.model.*;
import co.edu.unbosque.horarios.service.algorithm.*;
import co.edu.unbosque.horarios.service.algorithm.evaluator.SinTraslapeAulaEvaluator;
import org.junit.jupiter.api.Test;

import static co.edu.unbosque.horarios.testutil.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;

class HC02SinTraslapeAulaTest {

    private final SinTraslapeAulaEvaluator evaluador = new SinTraslapeAulaEvaluator();

    @Test
    void aulaLibreEnFranja() {
        HorarioContexto ctx = new HorarioContexto();
        assertTrue(evaluador.evaluate(
            new AsignacionCandidato(grupo(1), docente(1), aula(1), franja(5, "LUNES", "07:00")), ctx));
    }

    @Test
    void aulaOcupadaEnMismaFranja() {
        HorarioContexto ctx = new HorarioContexto();
        Aula a = aula(1);
        FranjaHoraria f = franja(5, "LUNES", "07:00");
        ctx.registrarAsignacion(new AsignacionCandidato(grupo(1), docente(1), a, f));

        assertFalse(evaluador.evaluate(
            new AsignacionCandidato(grupo(2), docente(2), a, f), ctx));
    }

    @Test
    void mismaAulaEnFranjaDiferente() {
        HorarioContexto ctx = new HorarioContexto();
        Aula a = aula(1);
        ctx.registrarAsignacion(new AsignacionCandidato(grupo(1), docente(1), a, franja(5, "LUNES", "07:00")));

        assertTrue(evaluador.evaluate(
            new AsignacionCandidato(grupo(2), docente(2), a, franja(6, "LUNES", "09:00")), ctx));
    }

    @Test
    void getHCId() {
        assertEquals("HC-02", evaluador.getHCId());
    }
}
