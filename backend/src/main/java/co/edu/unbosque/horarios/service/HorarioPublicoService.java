package co.edu.unbosque.horarios.service;

import co.edu.unbosque.horarios.dto.AsignacionDTO;
import co.edu.unbosque.horarios.dto.FranjaDTO;
import co.edu.unbosque.horarios.dto.GrupoDTO;
import co.edu.unbosque.horarios.dto.HorarioDTO;
import co.edu.unbosque.horarios.model.Docente;
import co.edu.unbosque.horarios.model.FranjaHoraria;
import co.edu.unbosque.horarios.model.Grupo;
import co.edu.unbosque.horarios.model.Horario;
import co.edu.unbosque.horarios.repository.AsignacionRepository;
import co.edu.unbosque.horarios.repository.DocenteRepository;
import co.edu.unbosque.horarios.repository.FranjaHorarioRepository;
import co.edu.unbosque.horarios.repository.GrupoRepository;
import co.edu.unbosque.horarios.repository.HorarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HorarioPublicoService {

    private final HorarioRepository horarioRepo;
    private final AsignacionRepository asignacionRepo;
    private final DocenteRepository docenteRepo;
    private final GrupoRepository grupoRepo;
    private final FranjaHorarioRepository franjaRepo;

    public HorarioDTO obtenerPublicado(String semestre) {
        Horario h = horarioRepo.findBySemestreAndEstado(semestre, "PUBLICADO")
            .orElseThrow(() -> new EntityNotFoundException("No hay horario publicado para " + semestre));
        List<AsignacionDTO> asignaciones = asignacionRepo.findByHorario(h)
            .stream().map(AsignacionDTO::from).toList();
        List<FranjaDTO> franjas = franjaRepo
            .findByParametroAndEsValidaTrue(h.getParametro())
            .stream().map(FranjaDTO::from).toList();
        return HorarioDTO.from(h, asignaciones, franjas);
    }

    public HorarioDTO obtenerPublicadoPorDocente(String semestre, Integer docenteId) {
        Horario h = horarioRepo.findBySemestreAndEstado(semestre, "PUBLICADO")
            .orElseThrow(() -> new EntityNotFoundException("No hay horario publicado para " + semestre));
        Docente docente = docenteRepo.findById(docenteId)
            .orElseThrow(() -> new EntityNotFoundException("Docente no encontrado: " + docenteId));
        List<AsignacionDTO> asignaciones = asignacionRepo.findByHorarioAndDocente(h, docente)
            .stream().map(AsignacionDTO::from).toList();
        List<FranjaDTO> franjas = franjaRepo
            .findByParametroAndEsValidaTrue(h.getParametro())
            .stream().map(FranjaDTO::from).toList();
        return HorarioDTO.from(h, asignaciones, franjas);
    }

    public List<GrupoDTO> listarGruposActivos() {
        return grupoRepo.findByEstado("ACTIVO").stream().map(GrupoDTO::from).toList();
    }

    public GrupoDTO obtenerGrupo(Integer grupoId) {
        Grupo g = grupoRepo.findById(grupoId)
            .orElseThrow(() -> new EntityNotFoundException("Grupo no encontrado: " + grupoId));
        return GrupoDTO.from(g);
    }
}
