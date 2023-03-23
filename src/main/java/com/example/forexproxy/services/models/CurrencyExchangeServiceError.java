package com.example.forexproxy.services.models;

import org.springframework.http.HttpStatusCode;

public class CurrencyExchangeServiceError extends RuntimeException {
    private String message;
    private HttpStatusCode status;

    public CurrencyExchangeServiceError(String message, HttpStatusCode status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatusCode getStatus() {
        return status;
    }
}
