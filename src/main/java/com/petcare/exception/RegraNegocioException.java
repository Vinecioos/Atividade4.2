package com.petcare.exception;

import org.springframework.http.HttpStatus;

/** Violação de regra de negócio (ex.: horário ocupado, veterinário não habilitado). */
public class RegraNegocioException extends RuntimeException {

    private final HttpStatus status;

    public RegraNegocioException(String mensagem, HttpStatus status) {
        super(mensagem);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
