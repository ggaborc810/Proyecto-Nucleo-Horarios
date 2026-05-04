# Entidades JPA

Cada entidad mapea 1:1 con una tabla del esquema (`docs/database.md`). Paquete: `co.edu.unbosque.horarios.model`.

## Convenciones

- Usar Lombok: `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`
- `@Entity @Table(name = "...")` con nombre en snake_case
- `@Id @GeneratedValue(strategy = GenerationType.IDENTITY)` para PKs
- Relaciones: `@ManyToOne`, `@OneToMany(mappedBy=...)`, `@ManyToMany` con tabla intermedia explícita

## TipoAula

```java
@Entity @Table(name = "tipo_aula")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TipoAula {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_aula")
    private Integer idTipoAula;
    
    @Column(name = "nombre_tipo", nullable = false, unique = true)
    private String nombreTipo;
}
```

## Aula

```java
@Entity @Table(name = "aula")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Aula {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aula_id")
    private Integer aulaId;
    
    @Column(name = "codigo_aula", nullable = false, unique = true)
    private String codigoAula;
    
    private Integer capacidad;
    private String ubicacion;
    private Boolean activa;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_aula", nullable = false)
    private TipoAula tipoAula;
}
```

## Docente

```java
@Entity @Table(name = "docente")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Docente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "docente_id")
    private Integer docenteId;
    
    @Column(name = "numero_documento", nullable = false, unique = true)
    private String numeroDocumento;
    
    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;
    
    @Column(name = "tipo_vinculacion", nullable = false)
    private String tipoVinculacion;  // TIEMPO_COMPLETO | TRES_CUARTOS | MEDIO_TIEMPO | CUARTO_TIEMPO
    
    @Column(name = "horas_max_semana", nullable = false)
    private Integer horasMaxSemana;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @OneToMany(mappedBy = "docente", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<DisponibilidadDocente> disponibilidades = new ArrayList<>();
}
```

## DisponibilidadDocente

```java
@Entity @Table(name = "disponibilidad_docente")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DisponibilidadDocente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "disponibilidad_id")
    private Integer disponibilidadId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_id", nullable = false)
    private Docente docente;
    
    @Column(name = "dia_semana", nullable = false)
    private String diaSemana;  // LUNES ... SABADO
    
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;
    
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;
    
    /** Método de dominio usado por HC-03 */
    public boolean cubre(FranjaHoraria franja) {
        return franja.getDiaSemana().equals(this.diaSemana)
            && !franja.getHoraInicio().isBefore(this.horaInicio)
            && !franja.getHoraValida().isAfter(this.horaFin);
    }
}
```

## Curso

```java
@Entity @Table(name = "curso")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Curso {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "curso_id")
    private Integer cursoId;
    
    @Column(name = "codigo_curso", nullable = false, unique = true)
    private String codigoCurso;
    
    @Column(name = "nombre_curso", nullable = false)
    private String nombreCurso;
    
    @Column(name = "frecuencia_semanal", nullable = false)
    private Integer frecuenciaSemanal;  // 1..4
    
    @Column(name = "semestre_nivel", nullable = false)
    private Integer semestreNivel;  // 1..10
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_aula", nullable = false)
    private TipoAula tipoAulaRequerida;  // HC-04
}
```

## CompatibilidadDocenteCurso

```java
@Entity @Table(name = "compatibilidad_docente_curso",
    uniqueConstraints = @UniqueConstraint(columnNames = {"docente_id", "curso_id"}))
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CompatibilidadDocenteCurso {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compatibilidad_id")
    private Integer compatibilidadId;
    
    @ManyToOne @JoinColumn(name = "docente_id", nullable = false)
    private Docente docente;
    
    @ManyToOne @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;
}
```

## ParametroSemestre

```java
@Entity @Table(name = "parametro_semestre")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ParametroSemestre {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_parametro")
    private Integer idParametro;
    
    @Column(nullable = false, unique = true)
    private String semestre;
    
    @Column(name = "franja_inicio_lv") private LocalTime franjaInicioLV;
    @Column(name = "franja_fin_lv")    private LocalTime franjaFinLV;
    @Column(name = "franja_inicio_sa") private LocalTime franjaInicioSA;
    @Column(name = "franja_fin_sa")    private LocalTime franjaFinSA;
    @Column(name = "exclusion_inicio") private LocalTime exclusionInicio;
    @Column(name = "exclusion_fin")    private LocalTime exclusionFin;
    
    @Column(name = "cap_max_grupo")   private Integer capMaxGrupo;
    @Column(name = "umbral_cierre")   private Integer umbralCierre;
    @Column(name = "freq_max_sesion") private Integer freqMaxSesion;
    
    private Boolean activo;
}
```

## FranjaHoraria

```java
@Entity @Table(name = "franja_horaria")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class FranjaHoraria {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "franja_id")
    private Integer franjaId;
    
    @Column(name = "dia_semana", nullable = false) private String diaSemana;
    @Column(name = "hora_inicio", nullable = false) private LocalTime horaInicio;
    @Column(name = "hora_valida", nullable = false) private LocalTime horaValida;  // hora_inicio + 2h
    @Column(name = "es_valida")  private Boolean esValida;
    
    @ManyToOne @JoinColumn(name = "id_parametro", nullable = false)
    private ParametroSemestre parametro;
}
```

## Grupo

```java
@Entity @Table(name = "grupo")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Grupo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grupo_id")
    private Integer grupoId;
    
    @Column(nullable = false) private String seccion;
    
    @Column(name = "num_inscritos") private Integer numInscritos;
    
    @Column(nullable = false) private String estado;  // ACTIVO | CERRADO
    
    @Column(name = "fecha_cierre") private LocalDate fechaCierre;
    @Column(name = "causa_cierre") private String causaCierre;
    
    @ManyToOne @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;
    
    @ManyToOne @JoinColumn(name = "docente_id", nullable = false)
    private Docente docente;
    
    public void cerrar(String causa) {
        this.estado = "CERRADO";
        this.fechaCierre = LocalDate.now();
        this.causaCierre = causa;
    }
}
```

## Horario

```java
@Entity @Table(name = "horario")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Horario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "horario_id")
    private Integer horarioId;
    
    @Column(nullable = false) private String semestre;
    @Column(nullable = false) private String estado;  // BORRADOR | PUBLICADO
    
    @Column(name = "fecha_generacion")  private LocalDateTime fechaGeneracion;
    @Column(name = "fecha_publicacion") private LocalDateTime fechaPublicacion;
    
    @ManyToOne @JoinColumn(name = "id_parametro")
    private ParametroSemestre parametro;
    
    public void publicar() {
        this.estado = "PUBLICADO";
        this.fechaPublicacion = LocalDateTime.now();
    }
}
```

## Asignacion

```java
@Entity @Table(name = "asignacion")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Asignacion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asignacion")
    private Integer idAsignacion;
    
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "grupo_id", nullable = false)
    private Grupo grupo;
    
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "aula_id")
    private Aula aula;  // null si conflicto
    
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "franja_id")
    private FranjaHoraria franja;
    
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "docente_id")
    private Docente docente;
    
    @ManyToOne @JoinColumn(name = "horario_id", nullable = false)
    private Horario horario;
    
    @Column(name = "fecha_asignacion")
    private LocalDateTime fechaAsignacion;
    
    @Column(name = "hc_violado")
    private String hcViolado;  // null = OK; "HC-01"..."HC-10" = conflicto
    
    @Column(nullable = false)
    private String estado;  // ASIGNADA | CONFLICTO | MANUAL
}
```

## Usuario

```java
@Entity @Table(name = "usuario")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Integer usuarioId;
    
    @Column(nullable = false, unique = true) private String username;
    @Column(nullable = false) private String password;  // BCrypt
    @Column(nullable = false) private String rol;  // ADMIN | DOCENTE
    
    @ManyToOne @JoinColumn(name = "docente_id")
    private Docente docente;  // solo si rol=DOCENTE
    
    private Boolean activo;
}
```
