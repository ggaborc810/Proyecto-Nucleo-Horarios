package co.edu.unbosque.horarios.dto;

public record ValidacionMovimientoDTO(boolean valido, String hcViolado, String mensajeError) {

    public static ValidacionMovimientoDTO ok() {
        return new ValidacionMovimientoDTO(true, null, null);
    }

    public static ValidacionMovimientoDTO conflicto(String hcId) {
        return new ValidacionMovimientoDTO(false, hcId, "Restricción violada: " + hcId);
    }
}
