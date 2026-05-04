package co.edu.unbosque.horarios.repository;

import co.edu.unbosque.horarios.model.CompatibilidadDocenteCurso;
import co.edu.unbosque.horarios.model.Curso;
import co.edu.unbosque.horarios.model.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CompatibilidadDocenteCursoRepository extends JpaRepository<CompatibilidadDocenteCurso, Integer> {
    boolean existsByDocenteAndCurso(Docente docente, Curso curso);
    List<CompatibilidadDocenteCurso> findByDocente(Docente docente);
    List<CompatibilidadDocenteCurso> findByCurso(Curso curso);

    @Transactional
    @Modifying
    void deleteByDocenteAndCurso(Docente docente, Curso curso);
}
