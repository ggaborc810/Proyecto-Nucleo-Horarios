package co.edu.unbosque.horarios.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "aula")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Aula {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aula_id")
    private Integer aulaId;

    @Column(name = "codigo_aula", nullable = false, unique = true)
    private String codigoAula;

    @Column(nullable = false)
    private Integer capacidad;

    private String ubicacion;

    @Builder.Default
    private Boolean activa = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_aula", nullable = false)
    private TipoAula tipoAula;
}
