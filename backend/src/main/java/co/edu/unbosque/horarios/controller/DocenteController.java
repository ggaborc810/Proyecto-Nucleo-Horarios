package co.edu.unbosque.horarios.controller;

import co.edu.unbosque.horarios.dto.CompatibilidadDTO;
import co.edu.unbosque.horarios.dto.DisponibilidadDTO;
import co.edu.unbosque.horarios.dto.DocenteDTO;
import co.edu.unbosque.horarios.service.DocenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/docentes")
@RequiredArgsConstructor
public class DocenteController {

    private final DocenteService docenteService;

    @GetMapping
    public List<DocenteDTO> listar() { return docenteService.listarTodos(); }

    @GetMapping("/{id}")
    public DocenteDTO obtener(@PathVariable Integer id) { return docenteService.obtener(id); }

    @PostMapping
    public ResponseEntity<DocenteDTO> crear(@Valid @RequestBody DocenteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(docenteService.crear(dto));
    }

    @PutMapping("/{id}")
    public DocenteDTO actualizar(@PathVariable Integer id, @Valid @RequestBody DocenteDTO dto) {
        return docenteService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        docenteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/disponibilidad")
    public List<DisponibilidadDTO> disponibilidades(@PathVariable Integer id) {
        return docenteService.listarDisponibilidades(id);
    }

    @PostMapping("/{id}/disponibilidad")
    public ResponseEntity<DisponibilidadDTO> registrarDisponibilidad(
            @PathVariable Integer id, @Valid @RequestBody DisponibilidadDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(docenteService.registrarDisponibilidad(id, dto));
    }

    @PutMapping("/{id}/disponibilidad/{dispId}")
    public DisponibilidadDTO actualizarDisponibilidad(@PathVariable Integer id,
                                                       @PathVariable Integer dispId,
                                                       @Valid @RequestBody DisponibilidadDTO dto) {
        return docenteService.actualizarDisponibilidad(id, dispId, dto);
    }

    @DeleteMapping("/{id}/disponibilidad/{dispId}")
    public ResponseEntity<Void> eliminarDisponibilidad(@PathVariable Integer id,
                                                        @PathVariable Integer dispId) {
        docenteService.eliminarDisponibilidad(id, dispId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/compatibilidades")
    public List<CompatibilidadDTO> compatibilidades(@PathVariable Integer id) {
        return docenteService.listarCompatibilidades(id);
    }

    @PostMapping("/{id}/compatibilidades")
    public ResponseEntity<CompatibilidadDTO> agregarCompatibilidad(
            @PathVariable Integer id, @RequestBody Map<String, Integer> body) {
        Integer cursoId = body.get("cursoId");
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(docenteService.agregarCompatibilidad(id, cursoId));
    }

    @DeleteMapping("/{id}/compatibilidades/{cursoId}")
    public ResponseEntity<Void> eliminarCompatibilidad(@PathVariable Integer id,
                                                        @PathVariable Integer cursoId) {
        docenteService.eliminarCompatibilidad(id, cursoId);
        return ResponseEntity.noContent().build();
    }
}
