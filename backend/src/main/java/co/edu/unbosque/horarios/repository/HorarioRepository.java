package co.edu.unbosque.horarios.repository;

import co.edu.unbosque.horarios.model.Horario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HorarioRepository extends JpaRepository<Horario, Integer> {
    Optional<Horario> findBySemestre(String semestre);
    Optional<Horario> findBySemestreAndEstado(String semestre, String estado);
    List<Horario> findByEstado(String estado);
}
