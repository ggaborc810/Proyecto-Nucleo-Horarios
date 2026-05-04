package co.edu.unbosque.horarios.dto;

import co.edu.unbosque.horarios.model.TipoAula;

public record TipoAulaDTO(Integer idTipoAula, String nombreTipo) {
    public static TipoAulaDTO from(TipoAula t) {
        return new TipoAulaDTO(t.getIdTipoAula(), t.getNombreTipo());
    }
}
