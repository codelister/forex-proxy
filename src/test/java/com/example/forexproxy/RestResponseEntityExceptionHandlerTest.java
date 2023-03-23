package com.example.forexproxy;

import com.example.forexproxy.services.models.CurrencyExchangeServiceError;
import com.example.forexproxy.models.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class RestResponseEntityExceptionHandlerTest {

    @Test
    void handleCurrencyExchangeServiceError() {
        WebRequest request = mock(WebRequest.class);
        CurrencyExchangeServiceError ex = new CurrencyExchangeServiceError("Service not available", HttpStatus.SERVICE_UNAVAILABLE );
        RestResponseEntityExceptionHandler handler = new RestResponseEntityExceptionHandler();
        ResponseEntity<Object> actual = handler.handleCurrencyExchangeServiceError(ex, request);
        ErrorResponse expected = new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, "Service not available");

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, actual.getStatusCode());
        ObjectMapper mapper = new ObjectMapper();
        try {
            ErrorResponse actualBody = mapper.readValue(mapper.writeValueAsString(actual.getBody()), ErrorResponse.class);
            assertEquals(expected, actualBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    void handleConstraintViolation() {
        WebRequest request = mock(WebRequest.class);
        RestResponseEntityExceptionHandler handler = new RestResponseEntityExceptionHandler();
        ConstraintViolationException ex = new ConstraintViolationException("Invalid request", null);
        ResponseEntity<Object> actual = handler.handleConstraintViolation(ex, request);
        ErrorResponse expected = new ErrorResponse(HttpStatus.BAD_REQUEST, "Invalid request");

        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
        ObjectMapper mapper = new ObjectMapper();
        try {
            ErrorResponse actualBody = mapper.readValue(mapper.writeValueAsString(actual.getBody()), ErrorResponse.class);
            assertEquals(expected, actualBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    void handleAllExceptions() {
        WebRequest request = mock(WebRequest.class);
        RestResponseEntityExceptionHandler handler = new RestResponseEntityExceptionHandler();
        RuntimeException ex = new RuntimeException("Invalid request");
        ResponseEntity<Object> actual = handler.handleAllExceptions(ex, request);
        ErrorResponse expected = new ErrorResponse(HttpStatus.BAD_REQUEST, "Invalid request");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actual.getStatusCode());
        ObjectMapper mapper = new ObjectMapper();
        try {
            ErrorResponse actualBody = mapper.readValue(mapper.writeValueAsString(actual.getBody()), ErrorResponse.class);
            assertEquals(expected, actualBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}