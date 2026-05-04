package co.edu.unbosque.horarios.service;

import co.edu.unbosque.horarios.dto.GrupoDTO;
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
public class GrupoService {

    private final GrupoRepository grupoRepo;
    private final CursoRepository cursoRepo;
    private final DocenteRepository docenteRepo;
    private final ParametroSemestreRepository paramRepo;
    private final CompatibilidadDocenteCursoRepository compatRepo;

    public List<GrupoDTO> listarTodos(String estado) {
        List<Grupo> grupos = (estado != null) ? grupoRepo.findByEstado(estado) : grupoRepo.findAll();
        return grupos.stream().map(GrupoDTO::from).toList();
    }

    public List<GrupoDTO> listarPorDocente(Integer docenteId) {
        Docente docente = docenteRepo.findById(docenteId)
            .orElseThrow(() -> new EntityNotFoundException("Docente no encontrado: " + docenteId));
        return grupoRepo.findByDocente(docente).stream().map(GrupoDTO::from).toList();
    }

    public GrupoDTO obtener(Integer id) {
        return grupoRepo.findById(id)
            .map(GrupoDTO::from)
            .orElseThrow(() -> new EntityNotFoundException("Grupo no encontrado: " + id));
    }

    @Transactional
    public GrupoDTO crear(GrupoDTO dto) {
        Curso curso = cursoRepo.findById(dto.cursoId())
            .orElseThrow(() -> new EntityNotFoundException("Curso no encontrado: " + dto.cursoId()));
        Docente docente = docenteRepo.findById(dto.docenteId())
            .orElseThrow(() -> new EntityNotFoundException("Docente no encontrado: " + dto.docenteId()));
        if (!compatRepo.existsByDocenteAndCurso(docente, curso)) {
            throw new IllegalStateException("El docente no es compatible con el curso (HC-10)");
        }
        Grupo grupo = Grupo.builder()
            .seccion(dto.seccion())
            .numInscritos(dto.numInscritos() != null ? dto.numInscritos() : 0)
            .curso(curso)
            .docente(docente)
            .build();
        return GrupoDTO.from(grupoRepo.save(grupo));
    }

    @Transactional
    public GrupoDTO actualizar(Integer id, GrupoDTO dto) {
        Grupo grupo = grupoRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Grupo no encontrado: " + id));
        grupo.setSeccion(dto.seccion());
        if (dto.numInscritos() != null) grupo.setNumInscritos(dto.numInscritos());

        if ("ACTIVO".equals(grupo.getEstado())) {
            paramRepo.findByActivoTrue().ifPresent(p -> {
                if (grupo.getNumInscritos() < p.getUmbralCierre()) {
                    grupo.cerrar("BAJA_INSCRIPCION_AUTOMATICA");
                }
            });
        }

        return GrupoDTO.from(grupoRepo.save(grupo));
    }

    @Transactional
    public GrupoDTO cerrarGrupo(Integer grupoId, String causa) {
        Grupo grupo = grupoRepo.findById(grupoId)
            .orElseThrow(() -> new EntityNotFoundException("Grupo no encontrado: " + grupoId));
        grupo.cerrar(causa);
        return GrupoDTO.from(grupoRepo.save(grupo));
    }

    @Transactional
    public GrupoDTO reabrirGrupo(Integer grupoId) {
        Grupo grupo = grupoRepo.findById(grupoId)
            .orElseThrow(() -> new EntityNotFoundException("Grupo no encontrado: " + grupoId));
        grupo.reabrir();
        return GrupoDTO.from(grupoRepo.save(grupo));
    }

    @Transactional
    public List<GrupoDTO> cerrarGruposAutomaticamente() {
        ParametroSemestre p = paramRepo.findByActivoTrue()
            .orElseThrow(() -> new EntityNotFoundException("No hay parámetro activo"));
        List<Grupo> aCerrar = grupoRepo.findByEstadoAndNumInscritosLessThan("ACTIVO", p.getUmbralCierre());
        aCerrar.forEach(g -> g.cerrar("BAJA_INSCRIPCION_AUTOMATICA"));
        grupoRepo.saveAll(aCerrar);
        return aCerrar.stream().map(GrupoDTO::from).toList();
    }
}
