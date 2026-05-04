# Controladores REST y WebSocket

Paquete: `co.edu.unbosque.horarios.controller`. Prefijo `/api`.

## Convenciones

- `@RestController` + `@RequestMapping("/api/...")`
- Validación con `@Valid` y Bean Validation en DTOs
- Inyección por constructor con `@RequiredArgsConstructor` (Lombok)
- Manejo global de excepciones en `GlobalExceptionHandler`

## HorarioController

```java
@RestController @RequestMapping("/api/horarios")
@RequiredArgsConstructor
public class HorarioController {
    private final HorarioService horarioService;

    @PostMapping("/generar")
    public ResponseEntity<ResultadoGeneracionDTO> generar(@RequestBody GenerarHorarioRequest req) {
        return ResponseEntity.ok(horarioService.generarHorario(req.semestre()));
    }

    @GetMapping("/{semestre}")
    public ResponseEntity<HorarioDTO> obtener(@PathVariable String semestre) {
        return ResponseEntity.ok(horarioService.obtenerPorSemestre(semestre));
    }

    @PutMapping("/{id}/publicar")
    public ResponseEntity<HorarioDTO> publicar(@PathVariable Integer id) {
        return ResponseEntity.ok(horarioService.publicarHorario(id));
    }

    @GetMapping("/{semestre}/conflictos")
    public ResponseEntity<List<ConflictoDTO>> conflictos(@PathVariable String semestre) {
        return ResponseEntity.ok(horarioService.obtenerConflictos(semestre));
    }
}
```

## DocenteController

```java
@RestController @RequestMapping("/api/docentes")
@RequiredArgsConstructor
public class DocenteController {
    private final DocenteService docenteService;

    @GetMapping
    public List<DocenteDTO> listar() { return docenteService.listarTodos(); }

    @GetMapping("/{id}")
    public DocenteDTO obtener(@PathVariable Integer id) { ... }

    @PostMapping
    public ResponseEntity<DocenteDTO> crear(@Valid @RequestBody DocenteDTO dto) { ... }

    @PutMapping("/{id}")
    public DocenteDTO actualizar(@PathVariable Integer id, @Valid @RequestBody DocenteDTO dto) { ... }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) { ... }

    @GetMapping("/{id}/disponibilidad")
    public List<DisponibilidadDTO> disponibilidades(@PathVariable Integer id) { ... }

    @PostMapping("/{id}/disponibilidad")
    public DisponibilidadDTO registrarDisponibilidad(@PathVariable Integer id,
                                                      @Valid @RequestBody DisponibilidadDTO dto) { ... }

    @DeleteMapping("/{id}/disponibilidad/{dispId}")
    public ResponseEntity<Void> eliminarDisponibilidad(@PathVariable Integer id,
                                                        @PathVariable Integer dispId) { ... }

    @GetMapping("/{id}/compatibilidades")
    public List<CompatibilidadDTO> compatibilidades(@PathVariable Integer id) { ... }

    @PostMapping("/{id}/compatibilidades")
    public CompatibilidadDTO agregarCompatibilidad(@PathVariable Integer id,
                                                    @RequestBody Map<String, Integer> body) { ... }
}
```

## AulaController, CursoController, GrupoController

CRUD estándar. Patrón análogo:

```java
@RestController @RequestMapping("/api/aulas")
public class AulaController {
    @GetMapping                      // List<AulaDTO>
    @GetMapping("/{id}")
    @GetMapping("/disponibles")      // ?franjaId=X
    @PostMapping
    @PutMapping("/{id}")
}

@RestController @RequestMapping("/api/cursos")
public class CursoController { /* CRUD */ }

@RestController @RequestMapping("/api/grupos")
public class GrupoController {
    @GetMapping                                  // ?estado=ACTIVO
    @GetMapping("/{id}")
    @PostMapping
    @PutMapping("/{id}")
    @PutMapping("/{id}/cerrar")
    @PostMapping("/cerrar-automatico")
}
```

## ParametroSemestreController

```java
@RestController @RequestMapping("/api/parametros")
@RequiredArgsConstructor
public class ParametroSemestreController {
    private final ParametroSemestreService paramService;

    @GetMapping
    public List<ParametroSemestreDTO> listar() { ... }

    @GetMapping("/activo")
    public ParametroSemestreDTO activo() { ... }

    @PostMapping
    public ResponseEntity<ParametroSemestreDTO> crear(@Valid @RequestBody ParametroSemestreDTO dto) {
        // El servicio auto-genera FranjaHoraria
        return ResponseEntity.status(HttpStatus.CREATED).body(paramService.crear(dto));
    }

    @PutMapping("/{id}")
    public ParametroSemestreDTO actualizar(@PathVariable Integer id,
                                            @Valid @RequestBody ParametroSemestreDTO dto) { ... }
}
```

## AsignacionController (Drag-and-Drop)

```java
@RestController @RequestMapping("/api/asignaciones")
@RequiredArgsConstructor
public class AsignacionController {
    private final AsignacionService asignacionService;

    @PutMapping("/{id}/mover")
    public AsignacionDTO mover(@PathVariable Integer id,
                                 @Valid @RequestBody MoverAsignacionRequest req) {
        return asignacionService.confirmarMovimiento(id, req.nuevaFranjaId(), req.nuevaAulaId());
    }

    @GetMapping("/validar-movimiento")
    public ValidacionMovimientoDTO validar(@RequestParam Integer asignacionId,
                                            @RequestParam Integer nuevaFranjaId,
                                            @RequestParam Integer nuevaAulaId) {
        return asignacionService.validarMovimiento(asignacionId, nuevaFranjaId, nuevaAulaId);
    }
}
```

## PublicoController (Sin autenticación)

```java
@RestController @RequestMapping("/api/publico")
@RequiredArgsConstructor
public class PublicoController {
    private final HorarioPublicoService publicoService;

    @GetMapping("/horario/{semestre}")
    public HorarioDTO horario(@PathVariable String semestre) {
        // Solo retorna horarios con estado=PUBLICADO
        return publicoService.obtenerPublicado(semestre);
    }

    @GetMapping("/horario/{semestre}/docente/{id}")
    public HorarioDTO porDocente(@PathVariable String semestre, @PathVariable Integer id) { ... }

    @GetMapping("/horario/{semestre}/aula/{id}")
    public HorarioDTO porAula(@PathVariable String semestre, @PathVariable Integer id) { ... }

    @GetMapping("/horario/{semestre}/curso/{id}")
    public HorarioDTO porCurso(@PathVariable String semestre, @PathVariable Integer id) { ... }
}
```

## AuthController

```java
@RestController @RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req.username(), req.password());
    }

    @GetMapping("/me")
    public UsuarioDTO me() {
        return authService.usuarioActual();
    }
}
```

## WebSocket — STOMP

### Configuración

```java
package co.edu.unbosque.horarios.websocket;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }
}
```

### Handler de Validación en Tiempo Real

```java
@Controller
@RequiredArgsConstructor
public class AsignacionWebSocketController {
    private final AsignacionService asignacionService;

    @MessageMapping("/validar-movimiento")
    @SendTo("/topic/validacion-movimiento")
    public ValidacionMovimientoDTO validar(MovimientoDTO mov) {
        return asignacionService.validarMovimiento(
            mov.asignacionId(), mov.nuevaFranjaId(), mov.nuevaAulaId()
        );
    }
}
```

## DTOs

Paquete `co.edu.unbosque.horarios.dto`. Usar `record` cuando sea posible.

```java
public record DocenteDTO(
    Integer docenteId,
    @NotBlank String numeroDocumento,
    @NotBlank String nombreCompleto,
    @NotBlank String tipoVinculacion,
    @Positive Integer horasMaxSemana,
    @Email String email,
    Integer totalDisponibilidades,
    Integer totalCompatibilidades
) {
    public static DocenteDTO from(Docente d) { ... }
}

public record AsignacionDTO(
    Integer idAsignacion, Integer grupoId, String seccionGrupo,
    String nombreCurso, String nombreDocente,
    Integer aulaId, String codigoAula,
    Integer franjaId, String diaSemana, String horaInicio, String horaFin,
    String estado, String hcViolado
) { ... }

public record HorarioDTO(
    Integer horarioId, String semestre, String estado,
    LocalDateTime fechaGeneracion, LocalDateTime fechaPublicacion,
    int totalConflictos, List<AsignacionDTO> asignaciones
) { ... }

public record ConflictoDTO(
    Integer grupoId, String seccion, String nombreCurso, String nombreDocente,
    int sesionNumero, String hcViolado, String descripcion,
    List<String> accionesCorrectivas
) { ... }

public record ResultadoGeneracionDTO(
    Integer horarioId, String semestre,
    int totalAsignadas, int totalConflictos,
    long tiempoEjecucionMs, List<ConflictoDTO> conflictos
) { ... }

public record ValidacionMovimientoDTO(
    boolean valido, String hcViolado, String mensajeError
) {
    public static ValidacionMovimientoDTO valido() {
        return new ValidacionMovimientoDTO(true, null, null);
    }
    public static ValidacionMovimientoDTO invalido(String hcId) {
        return new ValidacionMovimientoDTO(false, hcId, mensajePorHC(hcId));
    }
}

public record MovimientoDTO(Integer asignacionId, Integer nuevaFranjaId, Integer nuevaAulaId) {}

public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
public record LoginResponse(String token, String rol, String username) {}
```

## Manejo Global de Excepciones

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(HCVioladoException.class)
    public ResponseEntity<ErrorDTO> handleHC(HCVioladoException e) {
        return ResponseEntity.status(409).body(new ErrorDTO(e.getHcId(), e.getMessage(), null));
    }

    @ExceptionHandler(DatosMaestrosIncompletosException.class)
    public ResponseEntity<ErrorDTO> handleDatos(DatosMaestrosIncompletosException e) {
        return ResponseEntity.status(422).body(
            new ErrorDTO("DATOS_INCOMPLETOS", e.getMessage(), e.getRegistrosIncompletos())
        );
    }

    @ExceptionHandler(ConflictosPendientesException.class)
    public ResponseEntity<ErrorDTO> handleConflictos(ConflictosPendientesException e) {
        return ResponseEntity.status(409).body(new ErrorDTO("CONFLICTOS_PENDIENTES", e.getMessage(), null));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleNotFound(EntityNotFoundException e) {
        return ResponseEntity.status(404).body(new ErrorDTO("NOT_FOUND", e.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleValidation(MethodArgumentNotValidException e) {
        List<String> errores = e.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .toList();
        return ResponseEntity.badRequest().body(new ErrorDTO("VALIDATION_ERROR", "Errores de validación", errores));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGenerico(Exception e) {
        log.error("Error no manejado", e);
        return ResponseEntity.status(500).body(new ErrorDTO("INTERNAL_ERROR", "Error interno", null));
    }
}

public record ErrorDTO(String error, String mensaje, List<String> detalles) {}
```
