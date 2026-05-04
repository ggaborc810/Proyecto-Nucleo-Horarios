package co.edu.unbosque.horarios.algorithm;

import co.edu.unbosque.horarios.model.*;
import co.edu.unbosque.horarios.service.algorithm.*;
import co.edu.unbosque.horarios.service.algorithm.evaluator.SinTraslapeDocenteEvaluator;
import co.edu.unbosque.horarios.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;

import static co.edu.unbosque.horarios.testutil.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;

class HC01SinTraslapeDocenteTest {

    private final SinTraslapeDocenteEvaluator evaluador = new SinTraslapeDocenteEvaluator();

    @Test
    void docenteLibreEnFranja() {
        HorarioContexto ctx = new HorarioContexto();
        AsignacionCandidato c = new AsignacionCandidato(grupo(1), docente(1), aula(1), franja(5, "LUNES", "07:00"));
        assertTrue(evaluador.evaluate(c, ctx));
    }

    @Test
    void docenteOcupadoEnMismaFranja() {
        HorarioContexto ctx = new HorarioContexto();
        Docente d = docente(1);
        FranjaHoraria f = franja(5, "LUNES", "07:00");
        ctx.registrarAsignacion(new AsignacionCandidato(grupo(1), d, aula(1), f));

        assertFalse(evaluador.evaluate(new AsignacionCandidato(grupo(2), d, aula(2), f), ctx));
    }

    @Test
    void mismoDocenteEnFranjaDiferente() {
        HorarioContexto ctx = new HorarioContexto();
        Docente d = docente(1);
        ctx.registrarAsignacion(new AsignacionCandidato(grupo(1), d, aula(1), franja(5, "LUNES", "07:00")));

        assertTrue(evaluador.evaluate(
            new AsignacionCandidato(grupo(2), d, aula(2), franja(6, "LUNES", "09:00")), ctx));
    }

    @Test
    void getHCId() {
        assertEquals("HC-01", evaluador.getHCId());
    }
}
