package co.edu.unbosque.horarios.controller;

import co.edu.unbosque.horarios.dto.ConflictoDTO;
import co.edu.unbosque.horarios.dto.GenerarHorarioRequest;
import co.edu.unbosque.horarios.dto.HorarioDTO;
import co.edu.unbosque.horarios.dto.ResultadoGeneracionDTO;
import co.edu.unbosque.horarios.service.HorarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/horarios")
@RequiredArgsConstructor
public class HorarioController {

    private final HorarioService horarioService;

    @PostMapping("/generar")
    public ResponseEntity<ResultadoGeneracionDTO> generar(@Valid @RequestBody GenerarHorarioRequest req) {
        return ResponseEntity.ok(horarioService.generarHorario(req.semestre(), req.cursoIds()));
    }

    @GetMapping("/{semestre}")
    public ResponseEntity<HorarioDTO> obtener(@PathVariable String semestre) {
        return ResponseEntity.ok(horarioService.obtenerPorSemestre(semestre));
    }

    @PutMapping("/{id}/publicar")
    public ResponseEntity<HorarioDTO> publicar(@PathVariable Integer id) {
        return ResponseEntity.ok(horarioService.publicarHorario(id));
    }

    @GetMapping("/{semestre}/conflictos")
    public ResponseEntity<List<ConflictoDTO>> conflictos(@PathVariable String semestre) {
        return ResponseEntity.ok(horarioService.obtenerConflictos(semestre));
    }
}
