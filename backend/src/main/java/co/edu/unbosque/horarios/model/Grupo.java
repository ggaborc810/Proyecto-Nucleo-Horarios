package co.edu.unbosque.horarios.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "grupo")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Grupo {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grupo_id")
    private Integer grupoId;

    @Column(nullable = false)
    private String seccion;

    @Builder.Default
    @Column(name = "num_inscritos")
    private Integer numInscritos = 0;

    @Builder.Default
    @Column(nullable = false)
    private String estado = "ACTIVO";

    @Column(name = "fecha_cierre")
    private LocalDate fechaCierre;

    @Column(name = "causa_cierre")
    private String causaCierre;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "docente_id", nullable = false)
    private Docente docente;

    public void cerrar(String causa) {
        this.estado = "CERRADO";
        this.fechaCierre = LocalDate.now();
        this.causaCierre = causa;
    }

    public void reabrir() {
        this.estado = "ACTIVO";
        this.fechaCierre = null;
        this.causaCierre = null;
    }
}
