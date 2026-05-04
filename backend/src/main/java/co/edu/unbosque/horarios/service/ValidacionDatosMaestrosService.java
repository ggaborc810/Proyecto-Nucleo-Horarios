package co.edu.unbosque.horarios.service;

import co.edu.unbosque.horarios.exception.DatosMaestrosIncompletosException;
import co.edu.unbosque.horarios.model.TipoAula;
import co.edu.unbosque.horarios.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ValidacionDatosMaestrosService {

    private final DocenteRepository docenteRepo;
    private final GrupoRepository grupoRepo;
    private final AulaRepository aulaRepo;
    private final ParametroSemestreRepository paramRepo;
    private final CompatibilidadDocenteCursoRepository compatRepo;

    public void validarCompletitud(String semestre) {
        List<String> errores = new ArrayList<>();

        if (paramRepo.findBySemestre(semestre).isEmpty()) {
            errores.add("No existe ParametroSemestre para " + semestre);
        }

        docenteRepo.findAll().forEach(d -> {
            if (d.getDisponibilidades().isEmpty()) {
                errores.add("Docente " + d.getNombreCompleto() + " sin disponibilidad registrada");
            }
        });

        grupoRepo.findByEstado("ACTIVO").forEach(g -> {
            if (!compatRepo.existsByDocenteAndCurso(g.getDocente(), g.getCurso())) {
                errores.add("Grupo " + g.getSeccion() + ": docente "
                    + g.getDocente().getNombreCompleto()
                    + " no compatible con curso " + g.getCurso().getCodigoCurso());
            }
        });

        Set<TipoAula> tiposRequeridos = grupoRepo.findByEstado("ACTIVO").stream()
            .map(g -> g.getCurso().getTipoAulaRequerida())
            .collect(Collectors.toSet());

        tiposRequeridos.forEach(tipo -> {
            if (aulaRepo.findByTipoAulaAndActivaTrue(tipo).isEmpty()) {
                errores.add("Sin aulas activas del tipo " + tipo.getNombreTipo());
            }
        });

        if (!errores.isEmpty()) {
            throw new DatosMaestrosIncompletosException("Datos maestros incompletos", errores);
        }
    }
}
