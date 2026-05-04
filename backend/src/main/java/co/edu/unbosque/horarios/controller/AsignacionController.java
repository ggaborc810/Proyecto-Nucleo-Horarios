package co.edu.unbosque.horarios.controller;

import co.edu.unbosque.horarios.dto.AsignacionDTO;
import co.edu.unbosque.horarios.dto.MoverAsignacionRequest;
import co.edu.unbosque.horarios.dto.ValidacionMovimientoDTO;
import co.edu.unbosque.horarios.service.AsignacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/asignaciones")
@RequiredArgsConstructor
public class AsignacionController {

    private final AsignacionService asignacionService;

    @GetMapping("/{id}")
    public AsignacionDTO obtener(@PathVariable Integer id) {
        return asignacionService.obtener(id);
    }

    /** Implementación completa en Fase 4. */
    @PutMapping("/{id}/mover")
    public AsignacionDTO mover(@PathVariable Integer id,
                                @Valid @RequestBody MoverAsignacionRequest req) {
        return asignacionService.confirmarMovimiento(id, req.nuevaFranjaId(), req.nuevaAulaId());
    }

    /** Implementación completa en Fase 4. */
    @GetMapping("/validar-movimiento")
    public ValidacionMovimientoDTO validar(@RequestParam Integer asignacionId,
                                            @RequestParam Integer nuevaFranjaId,
                                            @RequestParam Integer nuevaAulaId) {
        return asignacionService.validarMovimiento(asignacionId, nuevaFranjaId, nuevaAulaId);
    }
}
