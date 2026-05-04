package co.edu.unbosque.horarios.repository;

import co.edu.unbosque.horarios.model.TipoAula;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoAulaRepository extends JpaRepository<TipoAula, Integer> {
    Optional<TipoAula> findByNombreTipo(String nombre);
}
