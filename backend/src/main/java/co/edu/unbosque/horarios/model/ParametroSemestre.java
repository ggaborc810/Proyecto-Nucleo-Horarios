package co.edu.unbosque.horarios.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "parametro_semestre")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ParametroSemestre {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_parametro")
    private Integer idParametro;

    @Column(nullable = false, unique = true)
    private String semestre;

    @Column(name = "franja_inicio_lv") private LocalTime franjaInicioLV;
    @Column(name = "franja_fin_lv")    private LocalTime franjaFinLV;
    @Column(name = "franja_inicio_sa") private LocalTime franjaInicioSA;
    @Column(name = "franja_fin_sa")    private LocalTime franjaFinSA;
    @Column(name = "exclusion_inicio") private LocalTime exclusionInicio;
    @Column(name = "exclusion_fin")    private LocalTime exclusionFin;

    @Column(name = "cap_max_grupo")   private Integer capMaxGrupo;
    @Column(name = "umbral_cierre")   private Integer umbralCierre;
    @Column(name = "freq_max_sesion") private Integer freqMaxSesion;

    @Builder.Default
    private Boolean activo = true;
}
