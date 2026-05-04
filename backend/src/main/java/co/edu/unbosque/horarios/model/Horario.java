package co.edu.unbosque.horarios.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "horario")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Horario {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "horario_id")
    private Integer horarioId;

    @Column(nullable = false)
    private String semestre;

    @Builder.Default
    @Column(nullable = false)
    private String estado = "BORRADOR";

    @Column(name = "fecha_generacion")
    private LocalDateTime fechaGeneracion;

    @Column(name = "fecha_publicacion")
    private LocalDateTime fechaPublicacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_parametro")
    private ParametroSemestre parametro;

    public void publicar() {
        this.estado = "PUBLICADO";
        this.fechaPublicacion = LocalDateTime.now();
    }
}
