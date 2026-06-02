package com.rafael.monitor_forno.exception;

public class SessaoEncerradaException extends RuntimeException {
    public SessaoEncerradaException(String mensagem) {
        super(mensagem);
    }
}
