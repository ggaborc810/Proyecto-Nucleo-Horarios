package co.edu.unbosque.horarios.dto;

import co.edu.unbosque.horarios.model.ParametroSemestre;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalTime;

public record ParametroSemestreDTO(
    Integer idParametro,
    @NotBlank String semestre,
    LocalTime franjaInicioLV,
    LocalTime franjaFinLV,
    LocalTime franjaInicioSA,
    LocalTime franjaFinSA,
    LocalTime exclusionInicio,
    LocalTime exclusionFin,
    Integer capMaxGrupo,
    Integer umbralCierre,
    Integer freqMaxSesion,
    Boolean activo
) {
    public static ParametroSemestreDTO from(ParametroSemestre p) {
        return new ParametroSemestreDTO(
            p.getIdParametro(), p.getSemestre(),
            p.getFranjaInicioLV(), p.getFranjaFinLV(),
            p.getFranjaInicioSA(), p.getFranjaFinSA(),
            p.getExclusionInicio(), p.getExclusionFin(),
            p.getCapMaxGrupo(), p.getUmbralCierre(), p.getFreqMaxSesion(),
            p.getActivo()
        );
    }

    public ParametroSemestre toEntity() {
        return ParametroSemestre.builder()
            .idParametro(idParametro)
            .semestre(semestre)
            .franjaInicioLV(franjaInicioLV != null ? franjaInicioLV : LocalTime.of(7, 0))
            .franjaFinLV(franjaFinLV != null ? franjaFinLV : LocalTime.of(22, 0))
            .franjaInicioSA(franjaInicioSA != null ? franjaInicioSA : LocalTime.of(7, 0))
            .franjaFinSA(franjaFinSA != null ? franjaFinSA : LocalTime.of(13, 0))
            .exclusionInicio(exclusionInicio != null ? exclusionInicio : LocalTime.of(12, 0))
            .exclusionFin(exclusionFin != null ? exclusionFin : LocalTime.of(13, 0))
            .capMaxGrupo(capMaxGrupo != null ? capMaxGrupo : 40)
            .umbralCierre(umbralCierre != null ? umbralCierre : 10)
            .freqMaxSesion(freqMaxSesion != null ? freqMaxSesion : 4)
            .activo(activo != null ? activo : true)
            .build();
    }
}
