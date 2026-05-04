package co.edu.unbosque.horarios.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "curso")
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
    private Integer frecuenciaSemanal;

    @Column(name = "semestre_nivel", nullable = false)
    private Integer semestreNivel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_aula", nullable = false)
    private TipoAula tipoAulaRequerida;

    /** Hora más temprana a la que puede iniciar una sesión de este curso (HC-11). */
    @Builder.Default
    @Column(name = "hora_inicio_permitida", nullable = false)
    private LocalTime horaInicioPerm = LocalTime.of(7, 0);

    /** Hora límite hasta la que puede terminar una sesión de este curso (HC-11). */
    @Builder.Default
    @Column(name = "hora_fin_permitida", nullable = false)
    private LocalTime horaFinPerm = LocalTime.of(22, 0);
}
