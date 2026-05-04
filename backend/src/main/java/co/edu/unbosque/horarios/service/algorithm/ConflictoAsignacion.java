package co.edu.unbosque.horarios.service.algorithm;

import co.edu.unbosque.horarios.model.Grupo;

/** Conflicto registrado cuando ningún candidato válido existe para una sesión requerida. */
public record ConflictoAsignacion(
    Grupo grupo,
    int sesionNumero,
    String hcViolado
) {}
