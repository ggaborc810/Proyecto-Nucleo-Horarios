package co.edu.unbosque.horarios.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class DatosMaestrosIncompletosException extends RuntimeException {
    private final List<String> registrosIncompletos;

    public DatosMaestrosIncompletosException(String msg, List<String> errores) {
        super(msg);
        this.registrosIncompletos = errores;
    }
}
