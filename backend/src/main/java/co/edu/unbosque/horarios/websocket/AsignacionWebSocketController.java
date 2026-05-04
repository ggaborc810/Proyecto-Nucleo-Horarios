package co.edu.unbosque.horarios.websocket;

import co.edu.unbosque.horarios.dto.MovimientoDTO;
import co.edu.unbosque.horarios.dto.ValidacionMovimientoDTO;
import co.edu.unbosque.horarios.service.AsignacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AsignacionWebSocketController {

    private final AsignacionService asignacionService;

    @MessageMapping("/validar-movimiento")
    @SendTo("/topic/validacion-movimiento")
    public ValidacionMovimientoDTO validar(MovimientoDTO mov) {
        return asignacionService.validarMovimiento(
            mov.asignacionId(), mov.nuevaFranjaId(), mov.nuevaAulaId()
        );
    }
}
