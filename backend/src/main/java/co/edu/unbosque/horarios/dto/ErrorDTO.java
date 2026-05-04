package co.edu.unbosque.horarios.dto;

import java.util.List;

public record ErrorDTO(String error, String mensaje, List<String> detalles) {}
