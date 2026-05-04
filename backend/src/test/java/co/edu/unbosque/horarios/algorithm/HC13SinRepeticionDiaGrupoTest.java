package co.edu.unbosque.horarios.algorithm;

import co.edu.unbosque.horarios.model.Grupo;
import co.edu.unbosque.horarios.service.algorithm.*;
import co.edu.unbosque.horarios.service.algorithm.evaluator.SinRepeticionBloqueGrupoEvaluator;
import org.junit.jupiter.api.Test;

import static co.edu.unbosque.horarios.testutil.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;

class HC13SinRepeticionDiaGrupoTest {

    private final SinRepeticionBloqueGrupoEvaluator evaluador = new SinRepeticionBloqueGrupoEvaluator();

    @Test
    void permiteOtraSesionEnDiaDistinto() {
        HorarioContexto ctx = new HorarioContexto();
        Grupo g = grupoConFrecuencia(1, 2);
        ctx.registrarAsignacion(new AsignacionCandidato(g, docente(1), aula(1), franja(1, "LUNES", "07:00")));

        assertTrue(evaluador.evaluate(
            new AsignacionCandidato(g, docente(1), aula(1), franja(2, "MARTES", "07:00")), ctx));
    }

    @Test
    void rechazaOtraSesionDelMismoGrupoEnElMismoDia() {
        HorarioContexto ctx = new HorarioContexto();
        Grupo g = grupoConFrecuencia(1, 2);
        ctx.registrarAsignacion(new AsignacionCandidato(g, docente(1), aula(1), franja(1, "LUNES", "07:00")));

        assertFalse(evaluador.evaluate(
            new AsignacionCandidato(g, docente(1), aula(1), franja(2, "LUNES", "15:00")), ctx));
    }

    @Test
    void getHCId() {
        assertEquals("HC-13", evaluador.getHCId());
    }
}
