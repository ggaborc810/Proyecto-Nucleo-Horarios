package co.edu.unbosque.horarios.controller;

import co.edu.unbosque.horarios.dto.TipoAulaDTO;
import co.edu.unbosque.horarios.service.TipoAulaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-aula")
@RequiredArgsConstructor
public class TipoAulaController {

    private final TipoAulaService tipoAulaService;

    @GetMapping
    public List<TipoAulaDTO> listar() {
        return tipoAulaService.listarTodos();
    }

    @GetMapping("/{id}")
    public TipoAulaDTO obtener(@PathVariable Integer id) {
        return tipoAulaService.obtener(id);
    }
}
