# Tests — Estrategia y Casos de Aceptación

## Principio Fundamental

El motor Greedy es un componente de dominio puro testeable de forma aislada:

- **NO** requiere `@SpringBootTest`
- **NO** requiere conexión a BD
- **SÍ** usa `@ExtendWith(MockitoExtension.class)`
- Suite completa del motor < 10 segundos

---

## 1. Tests Unitarios — HCEvaluators

Patrón base: un test class por evaluador.

### HC-01 — SinTraslapeDocenteEvaluator

```java
@ExtendWith(MockitoExtension.class)
class HC01SinTraslapeDocenteTest {

    private final SinTraslapeDocenteEvaluator evaluador = new SinTraslapeDocenteEvaluator();

    @Test
    void debeRetornarTrueCuandoDocenteEstaLibreEnFranja() {
        HorarioContexto ctx = new HorarioContexto();
        AsignacionCandidato c = new AsignacionCandidato(
            grupo(1), docente(1), aula(1), franja(5, "LUNES", "07:00")
        );
        assertTrue(evaluador.evaluate(c, ctx));
    }

    @Test
    void debeRetornarFalseCuandoDocenteYaTieneSesionEnMismaFranja() {
        HorarioContexto ctx = new HorarioContexto();
        Docente d = docente(1);
        FranjaHoraria f = franja(5, "LUNES", "07:00");

        ctx.registrarAsignacion(new AsignacionCandidato(grupo(1), d, aula(1), f));
        AsignacionCandidato candidato = new AsignacionCandidato(grupo(2), d, aula(2), f);

        assertFalse(evaluador.evaluate(candidato, ctx));
    }

    @Test
    void debePermitirMismoDocenteEnFranjasDiferentes() {
        HorarioContexto ctx = new HorarioContexto();
        Docente d = docente(1);
        ctx.registrarAsignacion(new AsignacionCandidato(grupo(1), d, aula(1), franja(5, "LUNES", "07:00")));

        AsignacionCandidato candidato = new AsignacionCandidato(grupo(2), d, aula(2), franja(6, "LUNES", "09:00"));
        assertTrue(evaluador.evaluate(candidato, ctx));
    }

    @Test
    void getHCIdRetornaHC01() {
        assertEquals("HC-01", evaluador.getHCId());
    }
}
```

### HC-02 — SinTraslapeAulaEvaluator

```java
@Test
void debeRechazarAulaOcupadaEnMismaFranja() {
    HorarioContexto ctx = new HorarioContexto();
    Aula a = aula(1);
    FranjaHoraria f = franja(5, "LUNES", "07:00");
    ctx.registrarAsignacion(new AsignacionCandidato(grupo(1), docente(1), a, f));

    AsignacionCandidato candidato = new AsignacionCandidato(grupo(2), docente(2), a, f);
    assertFalse(new SinTraslapeAulaEvaluator().evaluate(candidato, ctx));
}
```

### HC-03 — DisponibilidadDocenteEvaluator

```java
@Test
void debeAceptarFranjaDentroDeDisponibilidad() {
    Docente d = docenteConDisponibilidad("LUNES", "07:00", "13:00");
    FranjaHoraria f = franja(5, "LUNES", "07:00");  // 07:00-09:00
    AsignacionCandidato c = new AsignacionCandidato(null, d, null, f);
    assertTrue(new DisponibilidadDocenteEvaluator().evaluate(c, new HorarioContexto()));
}

@Test
void debeRechazarFranjaFueraDeDisponibilidad() {
    Docente d = docenteConDisponibilidad("LUNES", "07:00", "09:00");
    FranjaHoraria f = franja(6, "LUNES", "09:00");  // 09:00-11:00
    AsignacionCandidato c = new AsignacionCandidato(null, d, null, f);
    assertFalse(new DisponibilidadDocenteEvaluator().evaluate(c, new HorarioContexto()));
}

@Test
void debeRechazarFranjaDiaDiferente() {
    Docente d = docenteConDisponibilidad("LUNES", "07:00", "13:00");
    FranjaHoraria f = franja(10, "MARTES", "07:00");
    AsignacionCandidato c = new AsignacionCandidato(null, d, null, f);
    assertFalse(new DisponibilidadDocenteEvaluator().evaluate(c, new HorarioContexto()));
}
```

### HC-04 — TipoAulaCompatibleEvaluator

```java
@Test
void debeAceptarAulaDelTipoCorrecto() {
    TipoAula labComp = tipoAula(2, "LABORATORIO_COMPUTO");
    Curso curso = curso(1, labComp);
    Aula aula = aula(1, 30, labComp);
    AsignacionCandidato c = new AsignacionCandidato(grupo(curso), null, aula, null);

    assertTrue(new TipoAulaCompatibleEvaluator().evaluate(c, new HorarioContexto()));
}

@Test
void debeRechazarAulaDeOtroTipo() {
    TipoAula labComp = tipoAula(2, "LABORATORIO_COMPUTO");
    TipoAula conv   = tipoAula(1, "CONVENCIONAL");
    Curso curso = curso(1, labComp);
    Aula aula = aula(1, 30, conv);
    AsignacionCandidato c = new AsignacionCandidato(grupo(curso), null, aula, null);

    assertFalse(new TipoAulaCompatibleEvaluator().evaluate(c, new HorarioContexto()));
}
```

### HC-05 — CapacidadAulaEvaluator

```java
@Test
void debeAceptarCapacidadSuficiente() {
    Grupo g = grupo(1, 28);
    Aula a = aula(1, 35);
    assertTrue(new CapacidadAulaEvaluator().evaluate(
        new AsignacionCandidato(g, null, a, null), new HorarioContexto()));
}

@Test
void debeRechazarCapacidadInsuficiente() {
    assertFalse(new CapacidadAulaEvaluator().evaluate(
        new AsignacionCandidato(grupo(1, 36), null, aula(1, 35), null),
        new HorarioContexto()));
}

@Test
void debeAceptarCapacidadExacta() {
    assertTrue(new CapacidadAulaEvaluator().evaluate(
        new AsignacionCandidato(grupo(1, 35), null, aula(1, 35), null),
        new HorarioContexto()));
}
```

### HC-09 — FrecuenciaSemanalEvaluator

```java
@Test
void debeAceptarCuandoGrupoAunRequiereSesiones() {
    HorarioContexto ctx = new HorarioContexto();
    Grupo g = grupoConFrecuencia(1, 2);  // requiere 2 sesiones
    // 0 sesiones asignadas hasta ahora
    assertTrue(new FrecuenciaSemanalEvaluator().evaluate(
        new AsignacionCandidato(g, null, null, franja(5, "LUNES", "07:00")), ctx));
}

@Test
void debeRechazarCuandoGrupoYaCumplioFrecuencia() {
    HorarioContexto ctx = new HorarioContexto();
    Grupo g = grupoConFrecuencia(1, 2);
    // Registrar 2 sesiones previas
    ctx.registrarAsignacion(new AsignacionCandidato(g, docente(1), aula(1), franja(5, "LUNES", "07:00")));
    ctx.registrarAsignacion(new AsignacionCandidato(g, docente(1), aula(1), franja(7, "MARTES", "07:00")));

    assertFalse(new FrecuenciaSemanalEvaluator().evaluate(
        new AsignacionCandidato(g, null, null, franja(9, "MIERCOLES", "07:00")), ctx));
}
```

### HC-10 — CompatibilidadDocenteCursoEvaluator

```java
@ExtendWith(MockitoExtension.class)
class HC10Test {

    @Mock private CompatibilidadDocenteCursoRepository compatRepo;

    @Test
    void debeAceptarConCompatibilidadRegistrada() {
        Docente d = docente(1); Curso c = curso(1);
        when(compatRepo.existsByDocenteAndCurso(d, c)).thenReturn(true);

        var evaluador = new CompatibilidadDocenteCursoEvaluator(compatRepo);
        assertTrue(evaluador.evaluate(new AsignacionCandidato(grupo(c), d, null, null), new HorarioContexto()));
    }

    @Test
    void debeRechazarSinCompatibilidad() {
        when(compatRepo.existsByDocenteAndCurso(any(), any())).thenReturn(false);
        var evaluador = new CompatibilidadDocenteCursoEvaluator(compatRepo);
        assertFalse(evaluador.evaluate(new AsignacionCandidato(grupo(curso(1)), docente(1), null, null), new HorarioContexto()));
    }
}
```

---

## 2. Tests Unitarios — SoftEvaluators

```java
@Test
void sc01_debeDarMejorScoreParaSesionesNoConsecutivas() {
    HorarioContexto ctx = new HorarioContexto();
    Grupo g = grupo(1);
    // Grupo ya tiene sesión el LUNES
    ctx.registrarAsignacion(new AsignacionCandidato(g, docente(1), aula(1), franja(5, "LUNES", "07:00")));

    var ev = new SesionesNoConsecutivasSoftEvaluator();

    int scoreMartes = ev.score(new AsignacionCandidato(g, null, null, franja(7, "MARTES", "07:00")), ctx);
    int scoreJueves = ev.score(new AsignacionCandidato(g, null, null, franja(15, "JUEVES", "07:00")), ctx);

    assertTrue(scoreJueves > scoreMartes,
        "Jueves (no adyacente) debe tener mejor score que Martes (adyacente a Lunes)");
}

@Test
void sc02_debePenalizarAcumulacionEnMismoDia() {
    HorarioContexto ctx = new HorarioContexto();
    Docente d = docente(1);
    // Docente ya tiene 2 sesiones el LUNES
    ctx.registrarAsignacion(new AsignacionCandidato(grupo(1), d, aula(1), franja(5, "LUNES", "07:00")));
    ctx.registrarAsignacion(new AsignacionCandidato(grupo(2), d, aula(1), franja(6, "LUNES", "09:00")));

    var ev = new DistribucionCargaSoftEvaluator();

    int scoreLunes = ev.score(new AsignacionCandidato(null, d, null, franja(8, "LUNES", "13:00")), ctx);
    int scoreMartes = ev.score(new AsignacionCandidato(null, d, null, franja(11, "MARTES", "07:00")), ctx);

    assertTrue(scoreMartes > scoreLunes);
}
```

---

## 3. Tests Unitarios — SchedulerEngine

```java
@ExtendWith(MockitoExtension.class)
class SchedulerEngineTest {

    @Mock private DocenteRepository docenteRepo;
    @Mock private AulaRepository aulaRepo;
    @Mock private FranjaHorarioRepository franjaRepo;
    @Mock private GrupoRepository grupoRepo;
    @Mock private ParametroSemestreRepository paramRepo;
    @Mock private CompatibilidadDocenteCursoRepository compatRepo;

    private SchedulerEngine engine;

    @BeforeEach
    void setUp() {
        List<HCEvaluator> evaluadores = List.of(
            new SinTraslapeDocenteEvaluator(),
            new SinTraslapeAulaEvaluator(),
            new DisponibilidadDocenteEvaluator(),
            new TipoAulaCompatibleEvaluator(),
            new CapacidadAulaEvaluator(),
            new FranjaHorariaPermitidaEvaluator(),
            new DuracionFijaEvaluator(),
            new FrecuenciaSemanalEvaluator(),
            new CompatibilidadDocenteCursoEvaluator(compatRepo)
        );
        List<SoftEvaluator> soft = List.of(
            new SesionesNoConsecutivasSoftEvaluator(),
            new DistribucionCargaSoftEvaluator()
        );
        engine = new SchedulerEngine(evaluadores, soft, docenteRepo, aulaRepo, franjaRepo, grupoRepo, paramRepo, compatRepo);
    }

    @Test
    void debeAsignarTodasLasSessionesConRecursosSuficientes() {
        // 2 grupos, 2 aulas, docente disponible toda la semana, 2 sesiones c/u
        Docente d = docenteConDisponibilidadCompleta();
        TipoAula tipo = tipoAula(1, "CONVENCIONAL");
        Aula a1 = aula(1, 40, tipo); Aula a2 = aula(2, 40, tipo);
        Curso c = curso(1, 2, tipo);  // frecuencia=2
        Grupo g1 = grupo(1, c, d, 25); Grupo g2 = grupo(2, c, d, 20);

        when(grupoRepo.findByEstado("ACTIVO")).thenReturn(List.of(g1, g2));
        when(aulaRepo.findByActivaTrue()).thenReturn(List.of(a1, a2));
        when(franjaRepo.findByEsValidaTrue()).thenReturn(generarFranjas10Bloques());
        when(compatRepo.existsByDocenteAndCurso(any(), any())).thenReturn(true);
        when(paramRepo.findBySemestre(any())).thenReturn(Optional.of(parametros()));

        ResultadoGeneracion r = engine.ejecutar("2026-1", 1);

        assertEquals(4, r.asignacionesExitosas().size());  // 2 grupos × 2 sesiones
        assertEquals(0, r.conflictos().size());
        assertTrue(r.esCompleto());
    }

    @Test
    void debeReportarHC10CuandoNoHayCompatibilidad() {
        when(compatRepo.existsByDocenteAndCurso(any(), any())).thenReturn(false);
        // ... resto del setup

        ResultadoGeneracion r = engine.ejecutar("2026-1", 1);

        assertFalse(r.conflictos().isEmpty());
        assertEquals("HC-10", r.conflictos().get(0).hcViolado());
    }

    @Test
    void debeAsignarPrimeroGrupoConMasInscritos_P1() {
        // 2 grupos compitiendo por 1 aula+franja única disponible
        // g1 con 30 inscritos, g2 con 15
        // Esperar: g1 asignado, g2 en conflicto
    }

    @Test
    void debeRespetarLimiteDeIteraciones() {
        // Configurar caso imposible (0 aulas)
        when(aulaRepo.findByActivaTrue()).thenReturn(List.of());
        // ...
        ResultadoGeneracion r = engine.ejecutar("2026-1", 1);
        // Debe terminar (no colgar) y reportar conflicto
        assertFalse(r.conflictos().isEmpty());
    }

    @Test
    void debeFuncionarSinBaseDeDatosReal() {
        // Este test es de meta-arquitectura: si fallara con NoClassDefFoundError
        // de Hibernate o NullPointerException de PersistenceContext, hay un bug
        // de arquitectura (motor depende de infraestructura)
        // Solo Mockito, sin @SpringBootTest ni @DataJpaTest
    }
}
```

---

## 4. Tests Unitarios — GrupoService, HorarioService

```java
@ExtendWith(MockitoExtension.class)
class GrupoServiceTest {

    @Mock private GrupoRepository grupoRepo;
    @Mock private ParametroSemestreRepository paramRepo;
    @InjectMocks private GrupoService service;

    @Test
    void debeEvaluarCierreBajoUmbral() {
        when(paramRepo.findByActivoTrue()).thenReturn(Optional.of(parametros(10)));  // umbral=10
        Grupo g = grupo(1, 7);  // 7 inscritos
        assertTrue(service.evaluarCierre(g));
    }

    @Test
    void noDebeCerrarGrupoConInscriosSuficientes() {
        when(paramRepo.findByActivoTrue()).thenReturn(Optional.of(parametros(10)));
        assertFalse(service.evaluarCierre(grupo(1, 15)));
    }

    @Test
    void debeCerrarGrupoCorrectamente() {
        Grupo g = grupo(1, 7);
        when(grupoRepo.findById(1)).thenReturn(Optional.of(g));
        when(grupoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        GrupoDTO r = service.cerrarGrupo(1, "BAJA_INSCRIPCION");
        assertEquals("CERRADO", r.estado());
        assertNotNull(r.fechaCierre());
    }
}
```

---

## 5. Tests de Integración (Spring Boot)

Usan `@SpringBootTest` + `@ActiveProfiles("test")` + H2 en memoria.

```java
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class HorarioIntegrationTest {

    @Autowired private MockMvc mvc;
    @Autowired private ParametroSemestreRepository paramRepo;
    // ...

    @Test
    @WithMockUser(roles = "ADMIN")
    void debeGenerarHorarioCompletoConDatosReales() throws Exception {
        cargarDatosMinimos();

        mvc.perform(post("/api/horarios/generar")
                .contentType(APPLICATION_JSON)
                .content("""{ "semestre": "TEST-1" }"""))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalAsignadas").isNumber())
            .andExpect(jsonPath("$.tiempoEjecucionMs").value(lessThan(60000L)));
    }

    @Test
    void debeRetornar401SinJWT() throws Exception {
        mvc.perform(post("/api/horarios/generar")
                .contentType(APPLICATION_JSON)
                .content("""{"semestre":"TEST-1"}"""))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void debePermitirAccesoPublicoSinJWT() throws Exception {
        publicarHorarioPrueba();
        mvc.perform(get("/api/publico/horario/TEST-1"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void debeDetectar422DatosIncompletos() throws Exception {
        crearDocenteSinDisponibilidad();

        mvc.perform(post("/api/horarios/generar")
                .contentType(APPLICATION_JSON)
                .content("""{"semestre":"TEST-INCOMPLETO"}"""))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.error").value("DATOS_INCOMPLETOS"))
            .andExpect(jsonPath("$.detalles").isArray());
    }
}

class AsignacionDragDropIntegrationTest {

    @Test
    @WithMockUser(roles = "ADMIN")
    void debeRechazarMovimientoQueVioleHC01() throws Exception {
        // Estado inicial: docente d1 con sesión en LUNES 07:00 (asignación id=1)
        // y otra sesión en MARTES 07:00 (asignación id=2)
        // Intento mover id=2 a LUNES 07:00 → debe rechazar
        mvc.perform(put("/api/asignaciones/2/mover")
                .content("""{"nuevaFranjaId":5,"nuevaAulaId":2}""")
                .contentType(APPLICATION_JSON))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error").value("HC-01"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void debeConfirmarMovimientoValido() throws Exception {
        mvc.perform(put("/api/asignaciones/1/mover")
                .content("""{"nuevaFranjaId":8,"nuevaAulaId":2}""")
                .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("MANUAL"));
    }
}
```

---

## 6. Test de Rendimiento (RNF-01)

```java
@SpringBootTest
@ActiveProfiles("test")
class RendimientoMotorTest {

    @Autowired private SchedulerEngine engine;

    @Test
    void debeGenerar30GruposEnMenosDe60Segundos() {
        cargar30GruposConDatosCompletos();

        long t0 = System.currentTimeMillis();
        ResultadoGeneracion r = engine.ejecutar("TEST-PERF", 1);
        long ms = System.currentTimeMillis() - t0;

        assertTrue(ms < 60_000, "Tardó " + ms + "ms; debe ser < 60000ms");
        assertFalse(r.asignacionesExitosas().isEmpty());
    }
}
```

---

## 7. Tests de Seguridad

```java
@ParameterizedTest
@ValueSource(strings = {
    "/api/horarios/generar",
    "/api/docentes",
    "/api/aulas",
    "/api/cursos",
    "/api/grupos",
    "/api/parametros",
    "/api/asignaciones/1/mover"
})
void rutasAdminDebenRetornar401SinJWT(String ruta) throws Exception {
    mvc.perform(get(ruta)).andExpect(status().isUnauthorized());
}

@ParameterizedTest
@ValueSource(strings = { "/api/publico/horario/2026-1", "/api/auth/login" })
void rutasPublicasNoExigenJWT(String ruta) throws Exception {
    // Status puede ser 200 o 405 (POST en GET), pero NUNCA 401
    int status = mvc.perform(get(ruta)).andReturn().getResponse().getStatus();
    assertNotEquals(401, status);
}

@Test
@WithMockUser(roles = "DOCENTE")
void docenteRecibe403EnRutasAdmin() throws Exception {
    mvc.perform(get("/api/docentes")).andExpect(status().isForbidden());
}
```

---

## 8. Criterios de Aceptación — Checklist Final

### Motor (RF-01 a RF-04, RF-09)
- [ ] HC-01: cero asignaciones con mismo docente+franja
- [ ] HC-02: cero asignaciones con misma aula+franja
- [ ] HC-03: docentes solo en franjas de su disponibilidad
- [ ] HC-04: tipo de aula = tipo requerido por curso
- [ ] HC-05: capacidad ≥ inscritos en todas las asignaciones
- [ ] HC-06/07: todas las asignaciones en franjas válidas
- [ ] HC-08: cada sesión = 2 horas exactas
- [ ] HC-09: cada grupo recibe `frecuencia_semanal` sesiones o conflicto explicado
- [ ] HC-10: solo docentes compatibles asignados

### Reporte de Conflictos (RF-03)
- [ ] Cada conflicto identifica HC violado específico
- [ ] Cada conflicto incluye acciones correctivas
- [ ] Reporte se genera incluso con 0 conflictos (lista vacía)

### Datos Maestros (RF-05, RF-06)
- [ ] Generación bloqueada (422) si docente sin disponibilidad
- [ ] Generación bloqueada (422) si falta tipo de aula requerido
- [ ] Todos los parámetros configurables desde UI

### Grupos (RF-07, RF-08)
- [ ] Cierre automático detecta grupos bajo umbral
- [ ] Registro completo: fecha, causa, numInscritos al cerrar

### Interfaz Drag-and-Drop (RF-08, RNF-02)
- [ ] Feedback visual (verde/rojo) antes de soltar
- [ ] HC violado mostrado en < 2 segundos
- [ ] Usuario nuevo completa ajuste en < 5 minutos

### Seguridad (RF-09, RNF-04)
- [ ] `GET /api/publico/horario/2026-1` → 200 sin token
- [ ] `POST /api/horarios/generar` sin JWT → 401
- [ ] `POST /api/horarios/generar` con JWT DOCENTE → 403

### Rendimiento (RNF-01)
- [ ] 30 grupos activos → horario generado en < 60 segundos

### Calidad de Código (RNF-05, RNF-07)
- [ ] Tests del motor < 10 segundos sin `@SpringBootTest`
- [ ] `grep -r '"07:00"' src/main/java/co/edu/unbosque/horarios/service` → cero matches en lógica
- [ ] `grep -r 'capMaxGrupo *= *40' src/main/java` → cero matches
- [ ] `SchedulerEngine` no importa `javax.persistence`

---

## 9. Configuración de Test (`application-test.yml`)

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    database-platform: org.hibernate.dialect.H2Dialect

app:
  jwt:
    secret: test-secret-de-al-menos-32-caracteres-para-pruebas
    expiration: 3600000
  scheduler:
    max-iteraciones: 1000
    timeout-segundos: 30
```

---

## 10. Helpers de Test (recomendado en `src/test/java/.../testutil/`)

```java
public class TestDataFactory {
    public static Docente docente(int id) { ... }
    public static Docente docenteConDisponibilidad(String dia, String hi, String hf) { ... }
    public static Aula aula(int id) { ... }
    public static Aula aula(int id, int capacidad, TipoAula tipo) { ... }
    public static Grupo grupo(int id, int inscritos) { ... }
    public static FranjaHoraria franja(int id, String dia, String horaInicio) { ... }
    public static List<FranjaHoraria> generarFranjas10Bloques() { ... }
    public static ParametroSemestre parametros() { ... }
}
```

Importar estáticamente en cada test: `import static co.edu.unbosque.horarios.testutil.TestDataFactory.*;`
