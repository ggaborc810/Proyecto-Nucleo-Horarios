# SoftEvaluator — Restricciones Deseables

Las Soft Constraints (SC) actúan como **criterio de desempate** entre candidatos que ya pasaron todos los HC. Un candidato con score más alto se prefiere; uno con score bajo aún es válido.

## Interfaz

```java
public interface SoftEvaluator {
    /** Mayor puntaje = mejor candidato. */
    int score(AsignacionCandidato candidata, HorarioContexto contexto);

    /** Identificador: "SC-01", "SC-02" */
    String getSCId();
}
```

## SC-01 — Sesiones No Consecutivas

Para cursos con 2-3 sesiones semanales, es deseable que las sesiones del mismo grupo NO queden en días consecutivos.

```java
@Component
public class SesionesNoConsecutivasSoftEvaluator implements SoftEvaluator {

    private static final int PUNTAJE_BASE = 100;
    private static final int PENALIZACION_DIA_ADYACENTE = 50;

    @Override
    public int score(AsignacionCandidato c, HorarioContexto ctx) {
        List<FranjaHoraria> ya = ctx.getFranjasGrupo(c.getGrupo().getGrupoId());
        if (ya.isEmpty()) return PUNTAJE_BASE;

        int diaCandidato = ordinalDia(c.getFranja().getDiaSemana());
        int penalizacion = 0;

        for (FranjaHoraria f : ya) {
            int dia = ordinalDia(f.getDiaSemana());
            int distancia = Math.abs(diaCandidato - dia);
            if (distancia == 1) penalizacion += PENALIZACION_DIA_ADYACENTE;
            if (distancia == 0) penalizacion += PENALIZACION_DIA_ADYACENTE * 2; // mismo día
        }
        return PUNTAJE_BASE - penalizacion;
    }

    @Override public String getSCId() { return "SC-01"; }

    private int ordinalDia(String dia) {
        return switch (dia) {
            case "LUNES" -> 1; case "MARTES" -> 2; case "MIERCOLES" -> 3;
            case "JUEVES" -> 4; case "VIERNES" -> 5; case "SABADO" -> 6;
            default -> -1;
        };
    }
}
```

## SC-02 — Distribución Equilibrada de Carga

Es deseable que un docente no acumule todas sus sesiones en un mismo día.

```java
@Component
public class DistribucionCargaSoftEvaluator implements SoftEvaluator {

    private static final int PUNTAJE_BASE = 100;
    private static final int PENALIZACION_POR_SESION_MISMO_DIA = 25;

    @Override
    public int score(AsignacionCandidato c, HorarioContexto ctx) {
        List<FranjaHoraria> ya = ctx.getFranjasDocente(c.getDocente().getDocenteId());
        if (ya.isEmpty()) return PUNTAJE_BASE;

        String diaCandidato = c.getFranja().getDiaSemana();
        long sesionesMismoDia = ya.stream()
            .filter(f -> f.getDiaSemana().equals(diaCandidato))
            .count();

        int penalizacion = (int) (sesionesMismoDia * PENALIZACION_POR_SESION_MISMO_DIA);
        return PUNTAJE_BASE - penalizacion;
    }

    @Override public String getSCId() { return "SC-02"; }
}
```

## Combinación de Scores

En el motor, sumar los scores de todos los SoftEvaluators y elegir el candidato con mayor total:

```java
private AsignacionCandidato seleccionarMejor(List<AsignacionCandidato> candidatos,
                                              HorarioContexto ctx) {
    return candidatos.stream()
        .max(Comparator.comparingInt(c ->
            softEvaluators.stream()
                .mapToInt(se -> se.score(c, ctx))
                .sum()
        ))
        .orElseThrow();
}
```

## Diferencia HC vs SC

| Aspecto | HC | SC |
|---------|----|----|
| Si se viola | Asignación inválida — RECHAZAR | Asignación de menor calidad — OK |
| Tipo de retorno | `boolean` | `int` (score) |
| Cuándo se evalúa | Filtro PREVIO al paso | Solo entre candidatos válidos |
| Configurable | Sí, vía `ParametroSemestre` | Sí, vía constantes en evaluador |

## Tests

Ver `specs/tests.md`. Casos típicos:

- SC-01: dos sesiones del mismo grupo en días consecutivos → score menor que en días separados
- SC-02: docente con 3 sesiones en lunes → candidato en martes obtiene mejor score que candidato en lunes
