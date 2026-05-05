package co.edu.unbosque.horarios.security;

import co.edu.unbosque.horarios.dto.LoginResponse;
import co.edu.unbosque.horarios.dto.UsuarioDTO;
import co.edu.unbosque.horarios.model.Usuario;
import co.edu.unbosque.horarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepo;
    private final JwtUtil jwtUtil;

    public LoginResponse login(String username, String password) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Usuario o contraseña incorrectos.");
        }
        Usuario usuario = usuarioRepo.findByUsername(username).orElseThrow();
        String token = jwtUtil.generar(usuario);
        Integer docenteId = usuario.getDocente() != null ? usuario.getDocente().getDocenteId() : null;
        return new LoginResponse(token, usuario.getRol(), usuario.getUsername(), docenteId);
    }

    public UsuarioDTO usuarioActual() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario u = usuarioRepo.findByUsername(username).orElseThrow();
        return UsuarioDTO.from(u);
    }
}
