package co.edu.unbosque.horarios.service.algorithm;

import co.edu.unbosque.horarios.model.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** Value object que representa una asignación candidata durante el ciclo del motor. */
@Getter
@AllArgsConstructor
public class AsignacionCandidato {
    private final Grupo grupo;
    private final Docente docente;
    private final Aula aula;
    private final FranjaHoraria franja;
}
