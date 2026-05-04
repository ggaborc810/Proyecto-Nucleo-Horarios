package co.edu.unbosque.horarios.repository;

import co.edu.unbosque.horarios.model.Aula;
import co.edu.unbosque.horarios.model.TipoAula;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AulaRepository extends JpaRepository<Aula, Integer> {
    List<Aula> findByActivaTrue();
    List<Aula> findByCapacidadGreaterThanEqualAndActivaTrue(Integer capacidad);
    List<Aula> findByTipoAulaAndActivaTrue(TipoAula tipoAula);
}
