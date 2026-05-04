package co.edu.unbosque.horarios.algorithm;

import co.edu.unbosque.horarios.model.*;
import co.edu.unbosque.horarios.service.algorithm.*;
import co.edu.unbosque.horarios.service.algorithm.evaluator.TipoAulaCompatibleEvaluator;
import org.junit.jupiter.api.Test;

import static co.edu.unbosque.horarios.testutil.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;

class HC04TipoAulaTest {

    private final TipoAulaCompatibleEvaluator evaluador = new TipoAulaCompatibleEvaluator();

    @Test
    void aulaDelTipoCorrecto() {
        TipoAula lab = tipoAula(2, "LABORATORIO_COMPUTO");
        assertTrue(evaluador.evaluate(
            new AsignacionCandidato(grupo(curso(1, lab)), null, aula(1, 30, lab), null),
            new HorarioContexto()));
    }

    @Test
    void aulaDeOtroTipo() {
        TipoAula lab  = tipoAula(2, "LABORATORIO_COMPUTO");
        TipoAula conv = tipoAula(1, "CONVENCIONAL");
        assertFalse(evaluador.evaluate(
            new AsignacionCandidato(grupo(curso(1, lab)), null, aula(1, 30, conv), null),
            new HorarioContexto()));
    }

    @Test
    void getHCId() {
        assertEquals("HC-04", evaluador.getHCId());
    }
}
