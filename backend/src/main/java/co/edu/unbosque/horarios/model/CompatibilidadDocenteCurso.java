package co.edu.unbosque.horarios.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "compatibilidad_docente_curso",
    uniqueConstraints = @UniqueConstraint(columnNames = {"docente_id", "curso_id"}))
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CompatibilidadDocenteCurso {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compatibilidad_id")
    private Integer compatibilidadId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "docente_id", nullable = false)
    private Docente docente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;
}
