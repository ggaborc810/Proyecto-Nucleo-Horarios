package co.edu.unbosque.horarios.service.algorithm;

/** Interfaz para evaluadores de Soft Constraints usados en desempate. */
public interface SoftEvaluator {
    /** Score de preferencia — mayor es mejor. */
    int score(AsignacionCandidato candidato, HorarioContexto contexto);

    String getSCId();
}
