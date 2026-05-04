package co.edu.unbosque.horarios.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "franja_horaria")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class FranjaHoraria {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "franja_id")
    private Integer franjaId;

    @Column(name = "dia_semana", nullable = false)
    private String diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_valida", nullable = false)
    private LocalTime horaValida;

    @Builder.Default
    @Column(name = "es_valida")
    private Boolean esValida = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_parametro", nullable = false)
    private ParametroSemestre parametro;
}
