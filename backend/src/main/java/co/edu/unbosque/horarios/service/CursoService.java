package co.edu.unbosque.horarios.service;

import co.edu.unbosque.horarios.dto.CursoDTO;
import co.edu.unbosque.horarios.model.Curso;
import co.edu.unbosque.horarios.model.TipoAula;
import co.edu.unbosque.horarios.repository.CursoRepository;
import co.edu.unbosque.horarios.repository.TipoAulaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CursoService {

    private final CursoRepository cursoRepo;
    private final TipoAulaRepository tipoAulaRepo;

    public List<CursoDTO> listarTodos() {
        return cursoRepo.findAll().stream().map(CursoDTO::from).toList();
    }

    public CursoDTO obtener(Integer id) {
        return cursoRepo.findById(id)
            .map(CursoDTO::from)
            .orElseThrow(() -> new EntityNotFoundException("Curso no encontrado: " + id));
    }

    @Transactional
    public CursoDTO crear(CursoDTO dto) {
        TipoAula tipo = tipoAulaRepo.findById(dto.tipoAulaId())
            .orElseThrow(() -> new EntityNotFoundException("TipoAula no encontrado: " + dto.tipoAulaId()));
        Curso.CursoBuilder builder = Curso.builder()
            .codigoCurso(dto.codigoCurso())
            .nombreCurso(dto.nombreCurso())
            .frecuenciaSemanal(dto.frecuenciaSemanal())
            .semestreNivel(dto.semestreNivel())
            .tipoAulaRequerida(tipo);
        if (dto.horaInicioPerm() != null) builder.horaInicioPerm(dto.horaInicioPerm());
        if (dto.horaFinPerm()    != null) builder.horaFinPerm(dto.horaFinPerm());
        Curso curso = builder.build();
        return CursoDTO.from(cursoRepo.save(curso));
    }

    @Transactional
    public CursoDTO actualizar(Integer id, CursoDTO dto) {
        Curso curso = cursoRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Curso no encontrado: " + id));
        TipoAula tipo = tipoAulaRepo.findById(dto.tipoAulaId())
            .orElseThrow(() -> new EntityNotFoundException("TipoAula no encontrado: " + dto.tipoAulaId()));
        curso.setCodigoCurso(dto.codigoCurso());
        curso.setNombreCurso(dto.nombreCurso());
        curso.setFrecuenciaSemanal(dto.frecuenciaSemanal());
        curso.setSemestreNivel(dto.semestreNivel());
        curso.setTipoAulaRequerida(tipo);
        if (dto.horaInicioPerm() != null) curso.setHoraInicioPerm(dto.horaInicioPerm());
        if (dto.horaFinPerm()    != null) curso.setHoraFinPerm(dto.horaFinPerm());
        return CursoDTO.from(cursoRepo.save(curso));
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!cursoRepo.existsById(id)) throw new EntityNotFoundException("Curso no encontrado: " + id);
        cursoRepo.deleteById(id);
    }
}
