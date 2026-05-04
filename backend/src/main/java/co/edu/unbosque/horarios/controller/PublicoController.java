package co.edu.unbosque.horarios.controller;

import co.edu.unbosque.horarios.dto.GrupoDTO;
import co.edu.unbosque.horarios.dto.HorarioDTO;
import co.edu.unbosque.horarios.service.HorarioPublicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publico")
@RequiredArgsConstructor
public class PublicoController {

    private final HorarioPublicoService publicoService;

    @GetMapping("/horario/{semestre}")
    public HorarioDTO horario(@PathVariable String semestre) {
        return publicoService.obtenerPublicado(semestre);
    }

    @GetMapping("/horario/{semestre}/docente/{docenteId}")
    public HorarioDTO horarioPorDocente(@PathVariable String semestre,
                                         @PathVariable Integer docenteId) {
        return publicoService.obtenerPublicadoPorDocente(semestre, docenteId);
    }

    @GetMapping("/grupos")
    public List<GrupoDTO> grupos() {
        return publicoService.listarGruposActivos();
    }

    @GetMapping("/grupos/{id}")
    public GrupoDTO grupo(@PathVariable Integer id) {
        return publicoService.obtenerGrupo(id);
    }
}
