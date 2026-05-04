# Servicios de Aplicación

Paquete: `co.edu.unbosque.horarios.service`. Anotados con `@Service` y `@Transactional` cuando corresponde.

## HorarioService

Orquesta generación, publicación y consulta del horario.

```java
@Service @RequiredArgsConstructor
public class HorarioService {

    private final HorarioRepository horarioRepo;
    private final ParametroSemestreRepository paramRepo;
    private final AsignacionRepository asignacionRepo;
    private final SchedulerEngine schedulerEngine;
    private final ValidacionDatosMaestrosService validacionService;

    @Transactional
    public ResultadoGeneracionDTO generarHorario(String semestre) {
        // 1. Validar datos maestros completos
        validacionService.validarCompletitud(semestre);

        // 2. Crear o recuperar Horario en estado BORRADOR
        Horario horario = horarioRepo.findBySemestreAndEstado(semestre, "BORRADOR")
            .orElseGet(() -> crearBorrador(semestre));

        // 3. Limpiar asignaciones previas si existen
        asignacionRepo.deleteAll(asignacionRepo.findByHorario(horario));

        // 4. Ejecutar motor
        ResultadoGeneracion resultado = schedulerEngine.ejecutar(semestre, horario.getHorarioId());

        // 5. Persistir
        asignacionRepo.saveAll(resultado.asignacionesExitosas());
        horario.setFechaGeneracion(LocalDateTime.now());
        horarioRepo.save(horario);

        // 6. Mapear a DTO
        return ResultadoGeneracionDTO.from(horario, resultado);
    }

    @Transactional
    public HorarioDTO publicarHorario(Integer horarioId) {
        Horario horario = horarioRepo.findById(horarioId)
            .orElseThrow(() -> new EntityNotFoundException("Horario no encontrado"));

        long conflictos = asignacionRepo.findByHorarioAndHcVioladoIsNotNull(horario).size();
        if (conflictos > 0) {
            throw new ConflictosPendientesException("El horario tiene " + conflictos + " conflictos");
        }

        horario.publicar();
        horarioRepo.save(horario);
        return HorarioDTO.from(horario);
    }

    public HorarioDTO obtenerPorSemestre(String semestre) {
        Horario h = horarioRepo.findBySemestre(semestre)
            .orElseThrow(() -> new EntityNotFoundException("Sin horario para " + semestre));
        return HorarioDTO.from(h);
    }
}
```

## ValidacionDatosMaestrosService

Verifica que los datos maestros estén completos antes de generar.

```java
@Service @RequiredArgsConstructor
public class ValidacionDatosMaestrosService {

    private final DocenteRepository docenteRepo;
    private final GrupoRepository grupoRepo;
    private final AulaRepository aulaRepo;
    private final ParametroSemestreRepository paramRepo;
    private final CompatibilidadDocenteCursoRepository compatRepo;

    public void validarCompletitud(String semestre) {
        List<String> errores = new ArrayList<>();

        // 1. ParametroSemestre debe existir
        if (paramRepo.findBySemestre(semestre).isEmpty()) {
            errores.add("No existe ParametroSemestre para " + semestre);
        }

        // 2. Cada docente debe tener al menos 1 disponibilidad
        docenteRepo.findAll().forEach(d -> {
            if (d.getDisponibilidades().isEmpty()) {
                errores.add("Docente " + d.getNombreCompleto() + " no tiene disponibilidad registrada");
            }
        });

        // 3. Cada grupo activo debe tener docente compatible con el curso
        grupoRepo.findByEstado("ACTIVO").forEach(g -> {
            if (!compatRepo.existsByDocenteAndCurso(g.getDocente(), g.getCurso())) {
                errores.add("Grupo " + g.getSeccion() + ": docente " + g.getDocente().getNombreCompleto()
                    + " no es compatible con curso " + g.getCurso().getCodigoCurso());
            }
        });

        // 4. Para cada tipo de aula requerido por algún curso activo, debe existir aula activa
        Set<TipoAula> tiposRequeridos = grupoRepo.findByEstado("ACTIVO").stream()
            .map(g -> g.getCurso().getTipoAulaRequerida())
            .collect(Collectors.toSet());
        tiposRequeridos.forEach(tipo -> {
            if (aulaRepo.findByTipoAulaAndActivaTrue(tipo).isEmpty()) {
                errores.add("No hay aulas activas del tipo " + tipo.getNombreTipo());
            }
        });

        if (!errores.isEmpty()) {
            throw new DatosMaestrosIncompletosException("Datos incompletos", errores);
        }
    }
}
```

## ParametroSemestreService

Crea parámetros y **auto-genera** las franjas horarias.

```java
@Service @RequiredArgsConstructor
public class ParametroSemestreService {

    private final ParametroSemestreRepository paramRepo;
    private final FranjaHorarioRepository franjaRepo;

    @Transactional
    public ParametroSemestre crear(ParametroSemestre p) {
        ParametroSemestre guardado = paramRepo.save(p);
        generarFranjas(guardado);
        return guardado;
    }

    private void generarFranjas(ParametroSemestre p) {
        List<FranjaHoraria> franjas = new ArrayList<>();

        // Lunes a Viernes
        String[] diasLV = {"LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES"};
        for (String dia : diasLV) {
            franjas.addAll(generarFranjasDia(dia, p.getFranjaInicioLV(), p.getFranjaFinLV(), p));
        }
        // Sábado
        franjas.addAll(generarFranjasDia("SABADO", p.getFranjaInicioSA(), p.getFranjaFinSA(), p));

        franjaRepo.saveAll(franjas);
    }

    private List<FranjaHoraria> generarFranjasDia(String dia, LocalTime inicio, LocalTime fin,
                                                    ParametroSemestre p) {
        List<FranjaHoraria> resultado = new ArrayList<>();
        LocalTime cursor = inicio;
        while (!cursor.plusHours(2).isAfter(fin)) {
            LocalTime horaValida = cursor.plusHours(2);
            // Marca esValida según solapamiento con exclusión
            boolean solapa = cursor.isBefore(p.getExclusionFin())
                          && horaValida.isAfter(p.getExclusionInicio());
            FranjaHoraria f = FranjaHoraria.builder()
                .diaSemana(dia)
                .horaInicio(cursor)
                .horaValida(horaValida)
                .esValida(!solapa)
                .parametro(p)
                .build();
            resultado.add(f);
            cursor = horaValida;
        }
        return resultado;
    }
}
```

## DocenteService

```java
@Service @RequiredArgsConstructor
public class DocenteService {
    private final DocenteRepository docenteRepo;
    private final DisponibilidadDocenteRepository disponibilidadRepo;
    private final CompatibilidadDocenteCursoRepository compatRepo;
    private final ParametroSemestreRepository paramRepo;

    public List<DocenteDTO> listarTodos() { ... }
    public DocenteDTO crear(DocenteDTO dto) { ... }
    public DocenteDTO actualizar(Integer id, DocenteDTO dto) { ... }

    @Transactional
    public DisponibilidadDTO registrarDisponibilidad(Integer docenteId, DisponibilidadDTO dto) {
        // Validar que la franja declarada cae dentro de la franja permitida
        ParametroSemestre p = paramRepo.findByActivoTrue().orElseThrow();
        validarFranjaPermitida(dto, p);

        Docente docente = docenteRepo.findById(docenteId).orElseThrow();
        DisponibilidadDocente d = DisponibilidadDocente.builder()
            .docente(docente).diaSemana(dto.diaSemana())
            .horaInicio(dto.horaInicio()).horaFin(dto.horaFin())
            .build();
        return DisponibilidadDTO.from(disponibilidadRepo.save(d));
    }

    private void validarFranjaPermitida(DisponibilidadDTO dto, ParametroSemestre p) {
        // Si dia=SABADO, validar contra franjaInicioSA y franjaFinSA
        // Si dia=LUNES..VIERNES, validar contra franjaInicioLV y franjaFinLV
        // Si solapa con exclusion: rechazar
    }

    public boolean tieneCompatibilidad(Integer docenteId, Integer cursoId) {
        // ...
    }
}
```

## GrupoService

```java
@Service @RequiredArgsConstructor
public class GrupoService {
    private final GrupoRepository grupoRepo;
    private final ParametroSemestreRepository paramRepo;
    private final CompatibilidadDocenteCursoRepository compatRepo;

    @Transactional
    public GrupoDTO crear(GrupoDTO dto) {
        // Validar HC-10 al crear: docente debe ser compatible con curso
        // Validar que num_inscritos no supere cap_max_grupo × 1.1
        // ...
    }

    public boolean evaluarCierre(Grupo grupo) {
        ParametroSemestre p = paramRepo.findByActivoTrue().orElseThrow();
        return grupo.getNumInscritos() < p.getUmbralCierre();
    }

    @Transactional
    public GrupoDTO cerrarGrupo(Integer grupoId, String causa) {
        Grupo grupo = grupoRepo.findById(grupoId).orElseThrow();
        grupo.cerrar(causa);
        return GrupoDTO.from(grupoRepo.save(grupo));
    }

    @Transactional
    public List<GrupoDTO> cerrarGruposAutomaticamente() {
        ParametroSemestre p = paramRepo.findByActivoTrue().orElseThrow();
        List<Grupo> aCerrar = grupoRepo.findByEstadoAndNumInscritosLessThan(
            "ACTIVO", p.getUmbralCierre()
        );
        aCerrar.forEach(g -> g.cerrar("BAJA_INSCRIPCION_AUTOMATICA"));
        grupoRepo.saveAll(aCerrar);
        return aCerrar.stream().map(GrupoDTO::from).toList();
    }
}
```

## AsignacionService (Drag-and-Drop)

```java
@Service @RequiredArgsConstructor
public class AsignacionService {
    private final AsignacionRepository asignacionRepo;
    private final FranjaHorarioRepository franjaRepo;
    private final AulaRepository aulaRepo;
    private final List<HCEvaluator> hcEvaluators;

    public ValidacionMovimientoDTO validarMovimiento(Integer asignacionId,
                                                       Integer nuevaFranjaId,
                                                       Integer nuevaAulaId) {
        Asignacion a = asignacionRepo.findById(asignacionId).orElseThrow();
        FranjaHoraria nuevaFranja = franjaRepo.findById(nuevaFranjaId).orElseThrow();
        Aula nuevaAula = aulaRepo.findById(nuevaAulaId).orElseThrow();

        AsignacionCandidato candidato = new AsignacionCandidato(
            a.getGrupo(), a.getDocente(), nuevaAula, nuevaFranja
        );

        // Construir contexto SIN la asignación que se mueve
        HorarioContexto contexto = construirContextoExcluyendo(a);

        for (HCEvaluator ev : hcEvaluators) {
            if (!ev.evaluate(candidato, contexto)) {
                return ValidacionMovimientoDTO.invalido(ev.getHCId());
            }
        }
        return ValidacionMovimientoDTO.valido();
    }

    @Transactional
    public AsignacionDTO confirmarMovimiento(Integer asignacionId,
                                              Integer nuevaFranjaId,
                                              Integer nuevaAulaId) {
        ValidacionMovimientoDTO v = validarMovimiento(asignacionId, nuevaFranjaId, nuevaAulaId);
        if (!v.valido()) throw new HCVioladoException(v.hcViolado(), v.mensajeError());

        Asignacion a = asignacionRepo.findById(asignacionId).orElseThrow();
        a.setFranja(franjaRepo.findById(nuevaFranjaId).orElseThrow());
        a.setAula(aulaRepo.findById(nuevaAulaId).orElseThrow());
        a.setEstado("MANUAL");
        return AsignacionDTO.from(asignacionRepo.save(a));
    }

    private HorarioContexto construirContextoExcluyendo(Asignacion excluir) {
        HorarioContexto ctx = new HorarioContexto();
        asignacionRepo.findByHorarioAndHcVioladoIsNull(excluir.getHorario())
            .stream()
            .filter(a -> !a.getIdAsignacion().equals(excluir.getIdAsignacion()))
            .forEach(a -> ctx.registrarAsignacion(toCandidate(a)));
        return ctx;
    }
}
```

## AulaService, CursoService

CRUD estándar — patrón análogo a `DocenteService` sin disponibilidades. Implementación trivial con `@Service` + repositorio.

## Excepciones de Dominio

```java
package co.edu.unbosque.horarios.exception;

public class HCVioladoException extends RuntimeException {
    @Getter private final String hcId;
    public HCVioladoException(String hcId, String msg) { super(msg); this.hcId = hcId; }
}

public class ConflictosPendientesException extends RuntimeException { ... }

public class DatosMaestrosIncompletosException extends RuntimeException {
    @Getter private final List<String> registrosIncompletos;
    public DatosMaestrosIncompletosException(String msg, List<String> errores) {
        super(msg); this.registrosIncompletos = errores;
    }
}
```
