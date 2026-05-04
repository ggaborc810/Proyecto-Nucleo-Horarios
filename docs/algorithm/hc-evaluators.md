# HCEvaluator — 10 Hard Constraints (Patrón Strategy)

## Interfaz Base

```java
// co.edu.unbosque.horarios.service.algorithm.HCEvaluator
package co.edu.unbosque.horarios.service.algorithm;

public interface HCEvaluator {

    /**
     * Valida si la asignación candidata cumple esta restricción.
     * @return true si cumple, false si la viola
     */
    boolean evaluate(AsignacionCandidato candidata, HorarioContexto contexto);

    /** Identificador para el reporte de conflictos: "HC-01" ... "HC-10" */
    String getHCId();
}
```

## Value Objects

```java
// AsignacionCandidato — inmutable
public final class AsignacionCandidato {
    private final Grupo grupo;
    private final Docente docente;
    private final Aula aula;
    private final FranjaHoraria franja;
    // Constructor + getters
}

// HorarioContexto — estado mutable en memoria
public class HorarioContexto {
    // Mapas de ocupación O(1)
    private final Map<String, Boolean> franjaDocenteOcupada; // "docenteId_franjaId"
    private final Map<String, Boolean> franjaAulaOcupada;    // "aulaId_franjaId"
    private final Map<Integer, List<FranjaHoraria>> sesionesPorGrupo;
    private final Map<Integer, List<FranjaHoraria>> sesionesPorDocente;

    public boolean isDocenteOcupado(int docenteId, int franjaId) { ... }
    public boolean isAulaOcupada(int aulaId, int franjaId) { ... }
    public void registrarAsignacion(AsignacionCandidato a) { ... }
    public List<FranjaHoraria> getFranjasGrupo(int grupoId) { ... }
    public List<FranjaHoraria> getFranjasDocente(int docenteId) { ... }
}
```

## Implementaciones Requeridas

### HC-01 — Sin Traslape de Docente

```java
@Component
public class SinTraslapeDocenteEvaluator implements HCEvaluator {
    @Override
    public boolean evaluate(AsignacionCandidato c, HorarioContexto ctx) {
        return !ctx.isDocenteOcupado(c.getDocente().getDocenteId(),
                                      c.getFranja().getFranjaId());
    }
    @Override public String getHCId() { return "HC-01"; }
}
```

### HC-02 — Sin Traslape de Aula

```java
@Component
public class SinTraslapeAulaEvaluator implements HCEvaluator {
    @Override
    public boolean evaluate(AsignacionCandidato c, HorarioContexto ctx) {
        return !ctx.isAulaOcupada(c.getAula().getAulaId(),
                                   c.getFranja().getFranjaId());
    }
    @Override public String getHCId() { return "HC-02"; }
}
```

### HC-03 — Disponibilidad del Docente

```java
@Component
public class DisponibilidadDocenteEvaluator implements HCEvaluator {
    @Override
    public boolean evaluate(AsignacionCandidato c, HorarioContexto ctx) {
        return c.getDocente().getDisponibilidades().stream()
            .anyMatch(d -> d.cubre(c.getFranja()));
    }
    @Override public String getHCId() { return "HC-03"; }
}

// En la entidad DisponibilidadDocente:
public boolean cubre(FranjaHoraria franja) {
    return franja.getDiaSemana().equals(this.diaSemana)
        && !franja.getHoraInicio().isBefore(this.horaInicio)
        && !franja.getHoraValida().isAfter(this.horaFin);
}
```

### HC-04 — Compatibilidad Tipo de Aula

```java
@Component
public class TipoAulaCompatibleEvaluator implements HCEvaluator {
    @Override
    public boolean evaluate(AsignacionCandidato c, HorarioContexto ctx) {
        Integer tipoCurso = c.getGrupo().getCurso().getTipoAulaRequerida().getIdTipoAula();
        Integer tipoAula  = c.getAula().getTipoAula().getIdTipoAula();
        return tipoCurso.equals(tipoAula);
    }
    @Override public String getHCId() { return "HC-04"; }
}
```

### HC-05 — Capacidad del Aula

```java
@Component
public class CapacidadAulaEvaluator implements HCEvaluator {
    @Override
    public boolean evaluate(AsignacionCandidato c, HorarioContexto ctx) {
        return c.getAula().getCapacidad() >= c.getGrupo().getNumInscritos();
    }
    @Override public String getHCId() { return "HC-05"; }
}
```

### HC-06 — Franja Horaria Permitida

```java
@Component
public class FranjaHorariaPermitidaEvaluator implements HCEvaluator {
    @Override
    public boolean evaluate(AsignacionCandidato c, HorarioContexto ctx) {
        return Boolean.TRUE.equals(c.getFranja().getEsValida());
    }
    @Override public String getHCId() { return "HC-06"; }
}
```

### HC-07 — Exclusión de Mediodía

Cubierto por `es_valida = false` en franjas que solapan con `exclusion_inicio`-`exclusion_fin`. Evaluador adicional para defensa en profundidad:

```java
@Component
public class ExclusionMediodiaEvaluator implements HCEvaluator {

    private final ParametroSemestreRepository paramRepo;

    @Override
    public boolean evaluate(AsignacionCandidato c, HorarioContexto ctx) {
        ParametroSemestre p = paramRepo.findByActivoTrue().orElseThrow();
        FranjaHoraria f = c.getFranja();
        // Falla si el bloque solapa con el rango de exclusión
        boolean solapa = f.getHoraInicio().isBefore(p.getExclusionFin())
                      && f.getHoraValida().isAfter(p.getExclusionInicio());
        return !solapa;
    }
    @Override public String getHCId() { return "HC-07"; }
}
```

### HC-08 — Duración Fija de 2 Horas

Garantizado estructuralmente al pre-generar las franjas. El evaluador es defensivo:

```java
@Component
public class DuracionFijaEvaluator implements HCEvaluator {
    @Override
    public boolean evaluate(AsignacionCandidato c, HorarioContexto ctx) {
        Duration d = Duration.between(c.getFranja().getHoraInicio(),
                                       c.getFranja().getHoraValida());
        return d.toHours() == 2;
    }
    @Override public String getHCId() { return "HC-08"; }
}
```

### HC-09 — Frecuencia Semanal

```java
@Component
public class FrecuenciaSemanalEvaluator implements HCEvaluator {
    @Override
    public boolean evaluate(AsignacionCandidato c, HorarioContexto ctx) {
        int yaAsignadas = ctx.getFranjasGrupo(c.getGrupo().getGrupoId()).size();
        int requeridas = c.getGrupo().getCurso().getFrecuenciaSemanal();
        return yaAsignadas < requeridas;
    }
    @Override public String getHCId() { return "HC-09"; }
}
```

### HC-10 — Compatibilidad Docente-Curso

```java
@Component
public class CompatibilidadDocenteCursoEvaluator implements HCEvaluator {

    private final CompatibilidadDocenteCursoRepository compatRepo;

    @Override
    public boolean evaluate(AsignacionCandidato c, HorarioContexto ctx) {
        return compatRepo.existsByDocenteAndCurso(c.getDocente(), c.getGrupo().getCurso());
    }
    @Override public String getHCId() { return "HC-10"; }
}
```

**Optimización**: cargar el set de pares `(docenteId, cursoId)` compatibles UNA VEZ al inicio del ciclo y consultar en memoria.

## Inyección al Motor

```java
@Service
public class SchedulerEngine {
    private final List<HCEvaluator> hcEvaluators;
    
    // Spring inyecta automáticamente todos los @Component HCEvaluator
    public SchedulerEngine(List<HCEvaluator> hcEvaluators, ...) {
        this.hcEvaluators = hcEvaluators;
    }
}
```

## Patrón de Test (uno por evaluador)

Ver ejemplos completos en `specs/tests.md`. Cada test debe:
- Usar `@ExtendWith(MockitoExtension.class)` (NO `@SpringBootTest`)
- Verificar caso positivo, caso negativo, casos límite
- Ejecutar en milisegundos
