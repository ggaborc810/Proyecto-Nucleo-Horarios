package co.edu.unbosque.horarios.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String rol;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "docente_id")
    private Docente docente;

    @Builder.Default
    private Boolean activo = true;
}
