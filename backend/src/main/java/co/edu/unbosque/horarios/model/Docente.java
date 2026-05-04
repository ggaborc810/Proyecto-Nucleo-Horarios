package co.edu.unbosque.horarios.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "docente")
@Data
@NoArgsConstructor @AllArgsConstructor @Builder
@ToString(exclude = "disponibilidades")
@EqualsAndHashCode(exclude = "disponibilidades")
public class Docente {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "docente_id")
    private Integer docenteId;

    @Column(name = "numero_documento", nullable = false, unique = true)
    private String numeroDocumento;

    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;

    @Column(name = "tipo_vinculacion", nullable = false)
    private String tipoVinculacion;

    @Column(name = "horas_max_semana", nullable = false)
    private Integer horasMaxSemana;

    @Column(nullable = false, unique = true)
    private String email;

    @Builder.Default
    @OneToMany(mappedBy = "docente", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<DisponibilidadDocente> disponibilidades = new ArrayList<>();
}
