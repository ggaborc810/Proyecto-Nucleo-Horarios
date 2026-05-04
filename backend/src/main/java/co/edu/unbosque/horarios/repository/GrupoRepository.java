package co.edu.unbosque.horarios.repository;

import co.edu.unbosque.horarios.model.Curso;
import co.edu.unbosque.horarios.model.Docente;
import co.edu.unbosque.horarios.model.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GrupoRepository extends JpaRepository<Grupo, Integer> {
    List<Grupo> findByEstado(String estado);
    List<Grupo> findByCurso(Curso curso);
    List<Grupo> findByDocente(Docente docente);
    List<Grupo> findByEstadoAndNumInscritosLessThan(String estado, Integer umbral);
    List<Grupo> findByEstadoAndCursoCursoIdIn(String estado, List<Integer> cursoIds);
}
