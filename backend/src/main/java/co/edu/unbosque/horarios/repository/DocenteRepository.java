package co.edu.unbosque.horarios.repository;

import co.edu.unbosque.horarios.model.Docente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocenteRepository extends JpaRepository<Docente, Integer> {
    Optional<Docente> findByEmail(String email);
    List<Docente> findByTipoVinculacion(String tipo);
    Optional<Docente> findByNumeroDocumento(String numeroDocumento);
}
