package co.edu.unbosque.horarios.controller;

import co.edu.unbosque.horarios.dto.GrupoDTO;
import co.edu.unbosque.horarios.service.GrupoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grupos")
@RequiredArgsConstructor
public class GrupoController {

    private final GrupoService grupoService;

    @GetMapping
    public List<GrupoDTO> listar(@RequestParam(required = false) String estado,
                                  @RequestParam(required = false) Integer docenteId) {
        if (docenteId != null) return grupoService.listarPorDocente(docenteId);
        return grupoService.listarTodos(estado);
    }

    @GetMapping("/{id}")
    public GrupoDTO obtener(@PathVariable Integer id) { return grupoService.obtener(id); }

    @PostMapping
    public ResponseEntity<GrupoDTO> crear(@Valid @RequestBody GrupoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(grupoService.crear(dto));
    }

    @PutMapping("/{id}")
    public GrupoDTO actualizar(@PathVariable Integer id, @Valid @RequestBody GrupoDTO dto) {
        return grupoService.actualizar(id, dto);
    }

    @PutMapping("/{id}/cerrar")
    public GrupoDTO cerrar(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        String causa = body.getOrDefault("causa", "CIERRE_MANUAL");
        return grupoService.cerrarGrupo(id, causa);
    }

    @PutMapping("/{id}/reabrir")
    public GrupoDTO reabrir(@PathVariable Integer id) {
        return grupoService.reabrirGrupo(id);
    }

    @PostMapping("/cerrar-automatico")
    public List<GrupoDTO> cerrarAutomatico() {
        return grupoService.cerrarGruposAutomaticamente();
    }
}
