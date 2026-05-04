package co.edu.unbosque.horarios.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "disponibilidad_docente")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@ToString(exclude = "docente")
@EqualsAndHashCode(exclude = "docente")
public class DisponibilidadDocente {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "disponibilidad_id")
    private Integer disponibilidadId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_id", nullable = false)
    private Docente docente;

    @Column(name = "dia_semana", nullable = false)
    private String diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    /** Usado por HC-03 para verificar que la franja cae dentro de la disponibilidad declarada. */
    public boolean cubre(FranjaHoraria franja) {
        return franja.getDiaSemana().equals(this.diaSemana)
            && !franja.getHoraInicio().isBefore(this.horaInicio)
            && !franja.getHoraValida().isAfter(this.horaFin);
    }
}
