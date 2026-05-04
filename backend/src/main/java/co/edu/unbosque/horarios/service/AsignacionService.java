package co.edu.unbosque.horarios.service;

import co.edu.unbosque.horarios.dto.AsignacionDTO;
import co.edu.unbosque.horarios.dto.ValidacionMovimientoDTO;
import co.edu.unbosque.horarios.exception.HCVioladoException;
import co.edu.unbosque.horarios.model.*;
import co.edu.unbosque.horarios.repository.*;
import co.edu.unbosque.horarios.service.algorithm.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AsignacionService {

    private final AsignacionRepository               asignacionRepo;
    private final FranjaHorarioRepository            franjaRepo;
    private final AulaRepository                     aulaRepo;
    private final ParametroSemestreRepository        paramRepo;
    private final CompatibilidadDocenteCursoRepository compatRepo;
    private final List<HCEvaluator>                  hcEvaluators;

    public ValidacionMovimientoDTO validarMovimiento(Integer asignacionId,
                                                      Integer nuevaFranjaId,
                                                      Integer nuevaAulaId) {
        Asignacion asignacion = asignacionRepo.findById(asignacionId)
            .orElseThrow(() -> new EntityNotFoundException("Asignación no encontrada: " + asignacionId));
        FranjaHoraria nuevaFranja = franjaRepo.findById(nuevaFranjaId)
            .orElseThrow(() -> new EntityNotFoundException("Franja no encontrada: " + nuevaFranjaId));
        Aula nuevaAula = aulaRepo.findById(nuevaAulaId)
            .orElseThrow(() -> new EntityNotFoundException("Aula no encontrada: " + nuevaAulaId));

        Horario horario = asignacion.getHorario();
        if (!Boolean.TRUE.equals(nuevaFranja.getEsValida())) {
            return new ValidacionMovimientoDTO(false, "HC-06",
                "La franja destino no esta habilitada para programar clases.");
        }

        ParametroSemestre params = paramRepo.findBySemestre(horario.getSemestre()).orElse(null);
        if (params != null
                && nuevaFranja.getParametro() != null
                && !params.getIdParametro().equals(nuevaFranja.getParametro().getIdParametro())) {
            return new ValidacionMovimientoDTO(false, "HC-06",
                "La franja destino no pertenece al calendario configurado para este semestre.");
        }

        HorarioContexto contexto = construirContextoSin(horario, asignacionId);

        AsignacionCandidato candidato = new AsignacionCandidato(
            asignacion.getGrupo(), asignacion.getDocente(), nuevaAula, nuevaFranja
        );

        for (HCEvaluator hc : hcEvaluators) {
            if (!hc.evaluate(candidato, contexto)) {
                return ValidacionMovimientoDTO.conflicto(hc.getHCId());
            }
        }
        return ValidacionMovimientoDTO.ok();
    }

    @Transactional
    public AsignacionDTO confirmarMovimiento(Integer asignacionId,
                                              Integer nuevaFranjaId,
                                              Integer nuevaAulaId) {
        ValidacionMovimientoDTO validacion = validarMovimiento(asignacionId, nuevaFranjaId, nuevaAulaId);
        if (!validacion.valido()) {
            throw new HCVioladoException(validacion.hcViolado(),
                "Movimiento viola " + validacion.hcViolado());
        }

        Asignacion asignacion = asignacionRepo.findById(asignacionId).orElseThrow();
        FranjaHoraria nuevaFranja = franjaRepo.findById(nuevaFranjaId).orElseThrow();
        Aula nuevaAula = aulaRepo.findById(nuevaAulaId).orElseThrow();

        asignacion.setFranja(nuevaFranja);
        asignacion.setAula(nuevaAula);
        asignacion.setEstado("ASIGNADA");
        asignacion.setHcViolado(null);
        asignacion.setFechaAsignacion(LocalDateTime.now());

        return AsignacionDTO.from(asignacionRepo.save(asignacion));
    }

    public AsignacionDTO obtener(Integer id) {
        Asignacion a = asignacionRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Asignación no encontrada: " + id));
        return AsignacionDTO.from(a);
    }

    // ── construcción del contexto excluyendo la asignación que se mueve ──

    private HorarioContexto construirContextoSin(Horario horario, Integer excluirId) {
        ParametroSemestre params = paramRepo.findBySemestre(horario.getSemestre()).orElse(null);
        Set<String> compatibilidades = compatRepo.findAll().stream()
            .map(c -> c.getDocente().getDocenteId() + "_" + c.getCurso().getCursoId())
            .collect(Collectors.toSet());

        HorarioContexto contexto = new HorarioContexto(params, compatibilidades);

        asignacionRepo.findByHorarioAndHcVioladoIsNull(horario).stream()
            .filter(a -> !a.getIdAsignacion().equals(excluirId))
            .filter(a -> a.getDocente() != null && a.getFranja() != null && a.getAula() != null)
            .forEach(a -> contexto.registrarAsignacion(
                new AsignacionCandidato(a.getGrupo(), a.getDocente(), a.getAula(), a.getFranja())
            ));

        return contexto;
    }
}
