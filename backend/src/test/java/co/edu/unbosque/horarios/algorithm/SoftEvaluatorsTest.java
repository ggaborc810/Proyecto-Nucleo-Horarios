package co.edu.unbosque.horarios.algorithm;

import co.edu.unbosque.horarios.model.*;
import co.edu.unbosque.horarios.service.algorithm.*;
import co.edu.unbosque.horarios.service.algorithm.evaluator.*;
import org.junit.jupiter.api.Test;

import static co.edu.unbosque.horarios.testutil.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;

class SoftEvaluatorsTest {

    // ── SC-01 ─────────────────────────────────────────────────────────────

    @Test
    void sc01_primeraSessionDaPuntajeBase() {
        HorarioContexto ctx = new HorarioContexto();
        Grupo g = grupo(1);
        var ev = new SesionesNoConsecutivasSoftEvaluator();

        assertEquals(100, ev.score(
            new AsignacionCandidato(g, null, null, franja(5, "LUNES", "07:00")), ctx));
    }

    @Test
    void sc01_juevesEsMejorQueMartesCuandoHayLunes() {
        HorarioContexto ctx = new HorarioContexto();
        Grupo g = grupo(1);
        ctx.registrarAsignacion(new AsignacionCandidato(g, docente(1), aula(1), franja(5, "LUNES", "07:00")));

        var ev = new SesionesNoConsecutivasSoftEvaluator();
        int scoreMartes = ev.score(new AsignacionCandidato(g, null, null, franja(7, "MARTES", "07:00")), ctx);
        int scoreJueves = ev.score(new AsignacionCandidato(g, null, null, franja(15, "JUEVES", "07:00")), ctx);

        assertTrue(scoreJueves > scoreMartes,
            "Jueves (no adyacente) debe tener mejor score que Martes (adyacente a Lunes)");
    }

    @Test
    void sc01_mismoDiaPenalizaDoble() {
        HorarioContexto ctx = new HorarioContexto();
        Grupo g = grupo(1);
        ctx.registrarAsignacion(new AsignacionCandidato(g, docente(1), aula(1), franja(5, "LUNES", "07:00")));

        var ev = new SesionesNoConsecutivasSoftEvaluator();
        int scoreContig = ev.score(new AsignacionCandidato(g, null, null, franja(7, "MARTES", "07:00")), ctx);
        int scoreMismo  = ev.score(new AsignacionCandidato(g, null, null, franja(6, "LUNES", "09:00")), ctx);

        assertTrue(scoreContig > scoreMismo);
    }

    @Test
    void sc01GetId() {
        assertEquals("SC-01", new SesionesNoConsecutivasSoftEvaluator().getSCId());
    }

    // ── SC-02 ─────────────────────────────────────────────────────────────

    @Test
    void sc02_primeraSessionDaPuntajeBase() {
        HorarioContexto ctx = new HorarioContexto();
        Docente d = docente(1);
        var ev = new DistribucionCargaSoftEvaluator();

        assertEquals(100, ev.score(
            new AsignacionCandidato(null, d, null, franja(5, "LUNES", "07:00")), ctx));
    }

    @Test
    void sc02_martesMejorQueLunesSiDocenteTieneDosEnLunes() {
        HorarioContexto ctx = new HorarioContexto();
        Docente d = docente(1);
        ctx.registrarAsignacion(new AsignacionCandidato(grupo(1), d, aula(1), franja(5, "LUNES", "07:00")));
        ctx.registrarAsignacion(new AsignacionCandidato(grupo(2), d, aula(1), franja(6, "LUNES", "09:00")));

        var ev = new DistribucionCargaSoftEvaluator();
        int scoreLunes  = ev.score(new AsignacionCandidato(null, d, null, franja(8, "LUNES", "13:00")), ctx);
        int scoreMartes = ev.score(new AsignacionCandidato(null, d, null, franja(11, "MARTES", "07:00")), ctx);

        assertTrue(scoreMartes > scoreLunes);
    }

    @Test
    void sc02GetId() {
        assertEquals("SC-02", new DistribucionCargaSoftEvaluator().getSCId());
    }
}
