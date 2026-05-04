package co.edu.unbosque.horarios.controller;

import co.edu.unbosque.horarios.dto.CursoDTO;
import co.edu.unbosque.horarios.service.CursoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;

    @GetMapping
    public List<CursoDTO> listar() { return cursoService.listarTodos(); }

    @GetMapping("/{id}")
    public CursoDTO obtener(@PathVariable Integer id) { return cursoService.obtener(id); }

    @PostMapping
    public ResponseEntity<CursoDTO> crear(@Valid @RequestBody CursoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cursoService.crear(dto));
    }

    @PutMapping("/{id}")
    public CursoDTO actualizar(@PathVariable Integer id, @Valid @RequestBody CursoDTO dto) {
        return cursoService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        cursoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
