package br.upe.utils;

public class CustomRuntimeException extends RuntimeException {
    public CustomRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
