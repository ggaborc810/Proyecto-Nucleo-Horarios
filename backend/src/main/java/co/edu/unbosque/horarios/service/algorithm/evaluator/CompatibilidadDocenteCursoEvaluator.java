package co.edu.unbosque.horarios.service.algorithm.evaluator;

import co.edu.unbosque.horarios.repository.CompatibilidadDocenteCursoRepository;
import co.edu.unbosque.horarios.service.algorithm.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompatibilidadDocenteCursoEvaluator implements HCEvaluator {

    private final CompatibilidadDocenteCursoRepository compatRepo;

    @Override
    public boolean evaluate(AsignacionCandidato c, HorarioContexto ctx) {
        int docenteId = c.getDocente().getDocenteId();
        int cursoId   = c.getGrupo().getCurso().getCursoId();

        // Ruta rápida: set pre-cargado en contexto (ciclo del motor en producción)
        if (ctx.hasCompatibilidades()) {
            return ctx.isCompatible(docenteId, cursoId);
        }
        // Fallback para tests unitarios aislados con contexto vacío
        return compatRepo.existsByDocenteAndCurso(c.getDocente(), c.getGrupo().getCurso());
    }

    @Override
    public String getHCId() { return "HC-10"; }
}
