package co.edu.unbosque.horarios.repository;

import co.edu.unbosque.horarios.model.Curso;
import co.edu.unbosque.horarios.model.TipoAula;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CursoRepository extends JpaRepository<Curso, Integer> {
    Optional<Curso> findByCodigoCurso(String codigo);
    List<Curso> findByTipoAulaRequerida(TipoAula tipo);
    List<Curso> findBySemestreNivel(Integer semestreNivel);
}
