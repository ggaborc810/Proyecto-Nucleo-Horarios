package co.edu.unbosque.horarios.dto;

public record LoginResponse(String token, String rol, String username, Integer docenteId) {}
