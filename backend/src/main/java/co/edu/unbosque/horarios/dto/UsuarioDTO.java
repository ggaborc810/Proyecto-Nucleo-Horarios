package co.edu.unbosque.horarios.dto;

import co.edu.unbosque.horarios.model.Usuario;

public record UsuarioDTO(
    Integer usuarioId,
    String username,
    String rol,
    Integer docenteId,
    String nombreDocente,
    Boolean activo
) {
    public static UsuarioDTO from(Usuario u) {
        return new UsuarioDTO(
            u.getUsuarioId(), u.getUsername(), u.getRol(),
            u.getDocente() != null ? u.getDocente().getDocenteId() : null,
            u.getDocente() != null ? u.getDocente().getNombreCompleto() : null,
            u.getActivo()
        );
    }
}
