package co.edu.unbosque.horarios.exception;

import lombok.Getter;

@Getter
public class HCVioladoException extends RuntimeException {
    private final String hcId;

    public HCVioladoException(String hcId, String msg) {
        super(msg);
        this.hcId = hcId;
    }
}
