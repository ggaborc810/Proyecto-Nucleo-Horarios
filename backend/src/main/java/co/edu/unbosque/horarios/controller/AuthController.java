package co.edu.unbosque.horarios.controller;

import co.edu.unbosque.horarios.dto.LoginRequest;
import co.edu.unbosque.horarios.dto.LoginResponse;
import co.edu.unbosque.horarios.dto.UsuarioDTO;
import co.edu.unbosque.horarios.security.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req.username(), req.password());
    }

    @GetMapping("/me")
    public UsuarioDTO me() {
        return authService.usuarioActual();
    }
}
