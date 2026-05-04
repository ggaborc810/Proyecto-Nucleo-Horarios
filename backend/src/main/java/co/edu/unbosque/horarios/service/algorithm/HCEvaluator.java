package co.edu.unbosque.horarios.service.algorithm;

/** Interfaz Strategy para evaluadores de Hard Constraints. Implementada por HC-01..HC-10. */
public interface HCEvaluator {
    /** Retorna true si el candidato NO viola esta restricción. */
    boolean evaluate(AsignacionCandidato candidato, HorarioContexto contexto);

    /** Identificador de la restricción, e.g. "HC-01". */
    String getHCId();
}
