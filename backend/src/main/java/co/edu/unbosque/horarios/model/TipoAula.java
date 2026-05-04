package co.edu.unbosque.horarios.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipo_aula")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TipoAula {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_aula")
    private Integer idTipoAula;

    @Column(name = "nombre_tipo", nullable = false, unique = true)
    private String nombreTipo;
}
