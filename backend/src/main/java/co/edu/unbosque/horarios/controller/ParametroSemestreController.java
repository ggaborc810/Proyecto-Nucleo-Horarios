package co.edu.unbosque.horarios.controller;

import co.edu.unbosque.horarios.dto.FranjaDTO;
import co.edu.unbosque.horarios.dto.ParametroSemestreDTO;
import co.edu.unbosque.horarios.service.ParametroSemestreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parametros")
@RequiredArgsConstructor
public class ParametroSemestreController {

    private final ParametroSemestreService paramService;

    @GetMapping
    public List<ParametroSemestreDTO> listar() { return paramService.listarTodos(); }

    @GetMapping("/activo")
    public ParametroSemestreDTO activo() { return paramService.obtenerActivo(); }

    @GetMapping("/activo/franjas")
    public List<FranjaDTO> franjasActivo() { return paramService.listarFranjasActivo(); }

    @GetMapping("/{id}")
    public ParametroSemestreDTO obtener(@PathVariable Integer id) { return paramService.obtener(id); }

    @PostMapping
    public ResponseEntity<ParametroSemestreDTO> crear(@Valid @RequestBody ParametroSemestreDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paramService.crear(dto));
    }

    @PutMapping("/{id}")
    public ParametroSemestreDTO actualizar(@PathVariable Integer id,
                                           @Valid @RequestBody ParametroSemestreDTO dto) {
        return paramService.actualizar(id, dto);
    }

    @PostMapping("/{id}/franjas")
    public ResponseEntity<String> regenerarFranjas(@PathVariable Integer id) {
        int total = paramService.regenerarFranjas(id);
        return ResponseEntity.ok("Franjas generadas: " + total);
    }
}
