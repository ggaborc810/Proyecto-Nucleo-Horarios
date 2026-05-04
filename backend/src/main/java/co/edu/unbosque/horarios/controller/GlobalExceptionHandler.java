package co.edu.unbosque.horarios.controller;

import co.edu.unbosque.horarios.dto.ErrorDTO;
import co.edu.unbosque.horarios.exception.ConflictosPendientesException;
import co.edu.unbosque.horarios.exception.DatosMaestrosIncompletosException;
import co.edu.unbosque.horarios.exception.HCVioladoException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDTO> handleBadCreds(BadCredentialsException e) {
        return ResponseEntity.status(401)
            .body(new ErrorDTO("CREDENCIALES_INVALIDAS", e.getMessage(), null));
    }

    @ExceptionHandler(HCVioladoException.class)
    public ResponseEntity<ErrorDTO> handleHC(HCVioladoException e) {
        return ResponseEntity.status(409)
            .body(new ErrorDTO(e.getHcId(), e.getMessage(), null));
    }

    @ExceptionHandler(DatosMaestrosIncompletosException.class)
    public ResponseEntity<ErrorDTO> handleDatos(DatosMaestrosIncompletosException e) {
        return ResponseEntity.status(422)
            .body(new ErrorDTO("DATOS_INCOMPLETOS", e.getMessage(), e.getRegistrosIncompletos()));
    }

    @ExceptionHandler(ConflictosPendientesException.class)
    public ResponseEntity<ErrorDTO> handleConflictos(ConflictosPendientesException e) {
        return ResponseEntity.status(409)
            .body(new ErrorDTO("CONFLICTOS_PENDIENTES", e.getMessage(), null));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleNotFound(EntityNotFoundException e) {
        return ResponseEntity.status(404)
            .body(new ErrorDTO("NOT_FOUND", e.getMessage(), null));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorDTO> handleIllegalState(IllegalStateException e) {
        return ResponseEntity.status(409)
            .body(new ErrorDTO("CONFLICT", e.getMessage(), null));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDTO> handleIllegalArg(IllegalArgumentException e) {
        return ResponseEntity.status(400)
            .body(new ErrorDTO("BAD_REQUEST", e.getMessage(), null));
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ErrorDTO> handleNotImplemented(UnsupportedOperationException e) {
        return ResponseEntity.status(501)
            .body(new ErrorDTO("NOT_IMPLEMENTED", e.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleValidation(MethodArgumentNotValidException e) {
        List<String> errores = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .toList();
        return ResponseEntity.badRequest()
            .body(new ErrorDTO("VALIDATION_ERROR", "Errores de validación", errores));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGenerico(Exception e) {
        log.error("Error no manejado", e);
        return ResponseEntity.status(500)
            .body(new ErrorDTO("INTERNAL_ERROR", "Error interno del servidor", null));
    }
}
