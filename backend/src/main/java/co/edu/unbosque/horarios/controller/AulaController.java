package co.edu.unbosque.horarios.controller;

import co.edu.unbosque.horarios.dto.AulaDTO;
import co.edu.unbosque.horarios.service.AulaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aulas")
@RequiredArgsConstructor
public class AulaController {

    private final AulaService aulaService;

    @GetMapping
    public List<AulaDTO> listar() {
        return aulaService.listarTodos();
    }

    @GetMapping("/disponibles")
    public List<AulaDTO> disponibles() {
        return aulaService.listarActivas();
    }

    @GetMapping("/{id}")
    public AulaDTO obtener(@PathVariable Integer id) {
        return aulaService.obtener(id);
    }

    @PostMapping
    public ResponseEntity<AulaDTO> crear(@Valid @RequestBody AulaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(aulaService.crear(dto));
    }

    @PutMapping("/{id}")
    public AulaDTO actualizar(@PathVariable Integer id, @Valid @RequestBody AulaDTO dto) {
        return aulaService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        aulaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
