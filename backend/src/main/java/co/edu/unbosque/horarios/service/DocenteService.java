package co.edu.unbosque.horarios.service;

import co.edu.unbosque.horarios.dto.CompatibilidadDTO;
import co.edu.unbosque.horarios.dto.DisponibilidadDTO;
import co.edu.unbosque.horarios.dto.DocenteDTO;
import co.edu.unbosque.horarios.model.*;
import co.edu.unbosque.horarios.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocenteService {

    private final DocenteRepository docenteRepo;
    private final DisponibilidadDocenteRepository disponibilidadRepo;
    private final CompatibilidadDocenteCursoRepository compatRepo;
    private final CursoRepository cursoRepo;

    public List<DocenteDTO> listarTodos() {
        return docenteRepo.findAll().stream()
            .map(d -> DocenteDTO.from(d, (int) compatRepo.findByDocente(d).size()))
            .toList();
    }

    public DocenteDTO obtener(Integer id) {
        Docente d = docenteRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Docente no encontrado: " + id));
        return DocenteDTO.from(d, compatRepo.findByDocente(d).size());
    }

    @Transactional
    public DocenteDTO crear(DocenteDTO dto) {
        Docente docente = Docente.builder()
            .numeroDocumento(dto.numeroDocumento())
            .nombreCompleto(dto.nombreCompleto())
            .tipoVinculacion(dto.tipoVinculacion())
            .horasMaxSemana(dto.horasMaxSemana())
            .email(dto.email())
            .build();
        return DocenteDTO.from(docenteRepo.save(docente), 0);
    }

    @Transactional
    public DocenteDTO actualizar(Integer id, DocenteDTO dto) {
        Docente docente = docenteRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Docente no encontrado: " + id));
        docente.setNombreCompleto(dto.nombreCompleto());
        docente.setTipoVinculacion(dto.tipoVinculacion());
        docente.setHorasMaxSemana(dto.horasMaxSemana());
        docente.setEmail(dto.email());
        Docente saved = docenteRepo.save(docente);
        return DocenteDTO.from(saved, compatRepo.findByDocente(saved).size());
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!docenteRepo.existsById(id)) throw new EntityNotFoundException("Docente no encontrado: " + id);
        docenteRepo.deleteById(id);
    }

    public List<DisponibilidadDTO> listarDisponibilidades(Integer docenteId) {
        Docente d = docenteRepo.findById(docenteId)
            .orElseThrow(() -> new EntityNotFoundException("Docente no encontrado: " + docenteId));
        return disponibilidadRepo.findByDocente(d).stream().map(DisponibilidadDTO::from).toList();
    }

    @Transactional
    public DisponibilidadDTO registrarDisponibilidad(Integer docenteId, DisponibilidadDTO dto) {
        Docente docente = docenteRepo.findById(docenteId)
            .orElseThrow(() -> new EntityNotFoundException("Docente no encontrado: " + docenteId));
        DisponibilidadDocente disp = DisponibilidadDocente.builder()
            .docente(docente)
            .diaSemana(dto.diaSemana())
            .horaInicio(dto.horaInicio())
            .horaFin(dto.horaFin())
            .build();
        return DisponibilidadDTO.from(disponibilidadRepo.save(disp));
    }

    @Transactional
    public DisponibilidadDTO actualizarDisponibilidad(Integer docenteId, Integer dispId, DisponibilidadDTO dto) {
        DisponibilidadDocente disp = disponibilidadRepo.findById(dispId)
            .orElseThrow(() -> new EntityNotFoundException("Disponibilidad no encontrada: " + dispId));
        if (!disp.getDocente().getDocenteId().equals(docenteId)) {
            throw new IllegalArgumentException("La disponibilidad no pertenece al docente indicado");
        }
        disp.setHoraInicio(dto.horaInicio());
        disp.setHoraFin(dto.horaFin());
        disp.setDiaSemana(dto.diaSemana());
        return DisponibilidadDTO.from(disponibilidadRepo.save(disp));
    }

    @Transactional
    public void eliminarDisponibilidad(Integer docenteId, Integer dispId) {
        DisponibilidadDocente disp = disponibilidadRepo.findById(dispId)
            .orElseThrow(() -> new EntityNotFoundException("Disponibilidad no encontrada: " + dispId));
        if (!disp.getDocente().getDocenteId().equals(docenteId)) {
            throw new IllegalArgumentException("La disponibilidad no pertenece al docente indicado");
        }
        disponibilidadRepo.deleteById(dispId);
    }

    public List<CompatibilidadDTO> listarCompatibilidades(Integer docenteId) {
        Docente d = docenteRepo.findById(docenteId)
            .orElseThrow(() -> new EntityNotFoundException("Docente no encontrado: " + docenteId));
        return compatRepo.findByDocente(d).stream().map(CompatibilidadDTO::from).toList();
    }

    @Transactional
    public CompatibilidadDTO agregarCompatibilidad(Integer docenteId, Integer cursoId) {
        Docente docente = docenteRepo.findById(docenteId)
            .orElseThrow(() -> new EntityNotFoundException("Docente no encontrado: " + docenteId));
        Curso curso = cursoRepo.findById(cursoId)
            .orElseThrow(() -> new EntityNotFoundException("Curso no encontrado: " + cursoId));
        if (compatRepo.existsByDocenteAndCurso(docente, curso)) {
            throw new IllegalStateException("Ya existe compatibilidad entre el docente y el curso");
        }
        CompatibilidadDocenteCurso compat = CompatibilidadDocenteCurso.builder()
            .docente(docente).curso(curso).build();
        return CompatibilidadDTO.from(compatRepo.save(compat));
    }

    @Transactional
    public void eliminarCompatibilidad(Integer docenteId, Integer cursoId) {
        Docente docente = docenteRepo.findById(docenteId)
            .orElseThrow(() -> new EntityNotFoundException("Docente no encontrado: " + docenteId));
        Curso curso = cursoRepo.findById(cursoId)
            .orElseThrow(() -> new EntityNotFoundException("Curso no encontrado: " + cursoId));
        compatRepo.deleteByDocenteAndCurso(docente, curso);
    }
}
