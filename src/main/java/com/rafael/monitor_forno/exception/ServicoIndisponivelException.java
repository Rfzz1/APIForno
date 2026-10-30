package com.rafael.monitor_forno.exception;

public class ServicoIndisponivelException extends RuntimeException {
    public ServicoIndisponivelException(String message, Throwable causa) {
        super(message, causa);
    }
}
