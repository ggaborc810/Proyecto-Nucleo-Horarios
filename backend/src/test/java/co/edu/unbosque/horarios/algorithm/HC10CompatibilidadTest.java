package co.edu.unbosque.horarios.algorithm;

import co.edu.unbosque.horarios.model.*;
import co.edu.unbosque.horarios.repository.CompatibilidadDocenteCursoRepository;
import co.edu.unbosque.horarios.service.algorithm.*;
import co.edu.unbosque.horarios.service.algorithm.evaluator.CompatibilidadDocenteCursoEvaluator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static co.edu.unbosque.horarios.testutil.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HC10CompatibilidadTest {

    @Mock
    private CompatibilidadDocenteCursoRepository compatRepo;

    @Test
    void aceptaConCompatibilidadEnRepo() {
        Docente d = docente(1);
        Curso c   = curso(1);
        when(compatRepo.existsByDocenteAndCurso(d, c)).thenReturn(true);

        var evaluador = new CompatibilidadDocenteCursoEvaluator(compatRepo);
        assertTrue(evaluador.evaluate(
            new AsignacionCandidato(grupo(c), d, null, null), new HorarioContexto()));
    }

    @Test
    void rechazaSinCompatibilidadEnRepo() {
        when(compatRepo.existsByDocenteAndCurso(any(), any())).thenReturn(false);

        var evaluador = new CompatibilidadDocenteCursoEvaluator(compatRepo);
        assertFalse(evaluador.evaluate(
            new AsignacionCandidato(grupo(curso(1)), docente(1), null, null), new HorarioContexto()));
    }

    @Test
    void usaSetPreCargadoSiDisponible() {
        // Con set pre-cargado en contexto, NO llama al repo
        Set<String> compat = Set.of("1_1"); // docente 1 compatible con curso 1
        HorarioContexto ctx = new HorarioContexto(parametros(), compat);

        var evaluador = new CompatibilidadDocenteCursoEvaluator(compatRepo);
        assertTrue(evaluador.evaluate(
            new AsignacionCandidato(grupo(curso(1)), docente(1), null, null), ctx));

        verifyNoInteractions(compatRepo);
    }

    @Test
    void rechazaConSetPreCargadoSinPar() {
        Set<String> compat = Set.of("2_1"); // docente 2 compat con curso 1, no docente 1
        HorarioContexto ctx = new HorarioContexto(parametros(), compat);

        var evaluador = new CompatibilidadDocenteCursoEvaluator(compatRepo);
        assertFalse(evaluador.evaluate(
            new AsignacionCandidato(grupo(curso(1)), docente(1), null, null), ctx));

        verifyNoInteractions(compatRepo);
    }

    @Test
    void getHCId() {
        assertEquals("HC-10", new CompatibilidadDocenteCursoEvaluator(compatRepo).getHCId());
    }
}
