package com.example.forexproxy.models;

import org.springframework.http.HttpStatusCode;

public class ErrorResponse {
    private HttpStatusCode status;
    private String message;

    public ErrorResponse(HttpStatusCode status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatusCode getStatus() {
        return status;
    }

    public void setStatus(HttpStatusCode status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
