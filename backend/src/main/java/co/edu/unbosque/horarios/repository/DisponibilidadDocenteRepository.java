package co.edu.unbosque.horarios.repository;

import co.edu.unbosque.horarios.model.Docente;
import co.edu.unbosque.horarios.model.DisponibilidadDocente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DisponibilidadDocenteRepository extends JpaRepository<DisponibilidadDocente, Integer> {
    List<DisponibilidadDocente> findByDocente(Docente docente);
    List<DisponibilidadDocente> findByDocenteAndDiaSemana(Docente docente, String dia);

    @Transactional
    @Modifying
    void deleteByDocente(Docente docente);
}
