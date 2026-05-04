package co.edu.unbosque.horarios.repository;

import co.edu.unbosque.horarios.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AsignacionRepository extends JpaRepository<Asignacion, Integer> {
    List<Asignacion> findByHorario(Horario horario);
    List<Asignacion> findByHorarioAndHcVioladoIsNotNull(Horario horario);
    List<Asignacion> findByHorarioAndHcVioladoIsNull(Horario horario);

    Optional<Asignacion> findByDocenteAndFranjaAndEstado(Docente d, FranjaHoraria f, String estado);
    Optional<Asignacion> findByAulaAndFranjaAndEstado(Aula a, FranjaHoraria f, String estado);

    List<Asignacion> findByHorarioAndDocente(Horario horario, Docente docente);
    List<Asignacion> findByHorarioAndAula(Horario horario, Aula aula);

    @Query("SELECT a FROM Asignacion a WHERE a.horario = :horario AND a.grupo.curso = :curso")
    List<Asignacion> findByHorarioAndCurso(@Param("horario") Horario horario,
                                            @Param("curso") Curso curso);
}
