package co.edu.unbosque.horarios.repository;

import co.edu.unbosque.horarios.model.FranjaHoraria;
import co.edu.unbosque.horarios.model.ParametroSemestre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FranjaHorarioRepository extends JpaRepository<FranjaHoraria, Integer> {
    List<FranjaHoraria> findByEsValidaTrue();
    List<FranjaHoraria> findByDiaSemana(String dia);
    List<FranjaHoraria> findByParametro(ParametroSemestre parametro);
    List<FranjaHoraria> findByParametroAndEsValidaTrue(ParametroSemestre parametro);
}
