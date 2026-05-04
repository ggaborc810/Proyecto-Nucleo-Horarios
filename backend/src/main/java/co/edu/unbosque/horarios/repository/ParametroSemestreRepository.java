package co.edu.unbosque.horarios.repository;

import co.edu.unbosque.horarios.model.ParametroSemestre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParametroSemestreRepository extends JpaRepository<ParametroSemestre, Integer> {
    Optional<ParametroSemestre> findBySemestre(String semestre);
    Optional<ParametroSemestre> findByActivoTrue();
}
