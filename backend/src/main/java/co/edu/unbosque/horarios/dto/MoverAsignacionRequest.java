package co.edu.unbosque.horarios.dto;

import jakarta.validation.constraints.NotNull;

public record MoverAsignacionRequest(@NotNull Integer nuevaFranjaId, @NotNull Integer nuevaAulaId) {}
