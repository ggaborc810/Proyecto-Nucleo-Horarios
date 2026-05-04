package co.edu.unbosque.horarios.service;

import co.edu.unbosque.horarios.dto.AsignacionDTO;
import co.edu.unbosque.horarios.dto.ConflictoDTO;
import co.edu.unbosque.horarios.dto.HorarioDTO;
import co.edu.unbosque.horarios.dto.ResultadoGeneracionDTO;
import co.edu.unbosque.horarios.exception.ConflictosPendientesException;
import co.edu.unbosque.horarios.model.*;
import co.edu.unbosque.horarios.repository.*;
import co.edu.unbosque.horarios.service.algorithm.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HorarioService {

    private final HorarioRepository             horarioRepo;
    private final AsignacionRepository          asignacionRepo;
    private final ParametroSemestreRepository   paramRepo;
    private final SchedulerEngine               schedulerEngine;
    private final ValidacionDatosMaestrosService validacionService;

    @Transactional
    public ResultadoGeneracionDTO generarHorario(String semestre, List<Integer> cursoIds) {
        validacionService.validarCompletitud(semestre);

        ParametroSemestre params = paramRepo.findBySemestre(semestre)
            .orElseThrow(() -> new EntityNotFoundException("Sin parámetros para " + semestre));

        // Crear o reusar horario BORRADOR
        Horario horario = horarioRepo.findBySemestre(semestre)
            .orElseGet(() -> Horario.builder()
                .semestre(semestre)
                .estado("BORRADOR")
                .parametro(params)
                .build());
        horario.setFechaGeneracion(LocalDateTime.now());
        horario.setEstado("BORRADOR");
        horario = horarioRepo.save(horario);

        // Eliminar asignaciones previas
        List<Asignacion> previas = asignacionRepo.findByHorario(horario);
        if (!previas.isEmpty()) {
            asignacionRepo.deleteAll(previas);
            asignacionRepo.flush();
        }

        // Ejecutar motor
        ResultadoGeneracion resultado = schedulerEngine.ejecutar(semestre, horario.getHorarioId(), cursoIds);

        // Persistir asignaciones exitosas
        List<Asignacion> asignaciones = toAsignaciones(resultado.asignacionesExitosas(), horario);

        // Persistir conflictos como asignaciones con estado CONFLICTO
        List<Asignacion> conflictos = toConflictos(resultado.conflictos(), horario);

        asignaciones.addAll(conflictos);
        asignacionRepo.saveAll(asignaciones);

        List<ConflictoDTO> conflictoDTOs = conflictos.stream()
            .map(ConflictoDTO::from)
            .toList();

        return new ResultadoGeneracionDTO(
            horario.getHorarioId(),
            semestre,
            resultado.asignacionesExitosas().size(),
            resultado.conflictos().size(),
            resultado.tiempoEjecucionMs(),
            conflictoDTOs
        );
    }

    @Transactional
    public HorarioDTO publicarHorario(Integer horarioId) {
        Horario horario = horarioRepo.findById(horarioId)
            .orElseThrow(() -> new EntityNotFoundException("Horario no encontrado: " + horarioId));
        long conflictos = asignacionRepo.findByHorarioAndHcVioladoIsNotNull(horario).size();
        if (conflictos > 0) {
            throw new ConflictosPendientesException("El horario tiene " + conflictos + " conflictos sin resolver");
        }
        horario.publicar();
        Horario saved = horarioRepo.save(horario);
        List<AsignacionDTO> asigs = asignacionRepo.findByHorario(saved)
            .stream().map(AsignacionDTO::from).toList();
        return HorarioDTO.from(saved, asigs);
    }

    public HorarioDTO obtenerPorSemestre(String semestre) {
        Horario h = horarioRepo.findBySemestre(semestre)
            .orElseThrow(() -> new EntityNotFoundException("Sin horario para " + semestre));
        List<AsignacionDTO> asigs = asignacionRepo.findByHorario(h)
            .stream().map(AsignacionDTO::from).toList();
        return HorarioDTO.from(h, asigs);
    }

    public List<ConflictoDTO> obtenerConflictos(String semestre) {
        Horario h = horarioRepo.findBySemestre(semestre)
            .orElseThrow(() -> new EntityNotFoundException("Sin horario para " + semestre));
        return asignacionRepo.findByHorarioAndHcVioladoIsNotNull(h)
            .stream().map(ConflictoDTO::from).toList();
    }

    // ── conversión en memoria → entidades JPA ────────────────────────────

    private List<Asignacion> toAsignaciones(List<AsignacionCandidato> exitosas, Horario horario) {
        List<Asignacion> result = new ArrayList<>();
        for (AsignacionCandidato c : exitosas) {
            result.add(Asignacion.builder()
                .grupo(c.getGrupo())
                .docente(c.getDocente())
                .aula(c.getAula())
                .franja(c.getFranja())
                .horario(horario)
                .fechaAsignacion(LocalDateTime.now())
                .estado("ASIGNADA")
                .build());
        }
        return result;
    }

    private List<Asignacion> toConflictos(List<ConflictoAsignacion> conflictos, Horario horario) {
        List<Asignacion> result = new ArrayList<>();
        for (ConflictoAsignacion c : conflictos) {
            result.add(Asignacion.builder()
                .grupo(c.grupo())
                .docente(c.grupo().getDocente())
                .horario(horario)
                .fechaAsignacion(LocalDateTime.now())
                .hcViolado(c.hcViolado())
                .estado("CONFLICTO")
                .build());
        }
        return result;
    }
}
