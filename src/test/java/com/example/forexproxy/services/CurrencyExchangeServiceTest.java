package com.example.forexproxy.services;

import com.example.forexproxy.services.models.CurrencyExchangeServiceError;
import com.example.forexproxy.services.models.ExchangeRate;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CurrencyExchangeServiceTest {

    private MockWebServer mockWebServer;
    private CurrencyExchangeService currencyExchangeService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        currencyExchangeService = new CurrencyExchangeService(baseUrl, "test-token");
    }

    @Test
    void getExchangeRate_returnsExpectedResponse() {
        String mockResponseBody = """
                [
                    {
                        "from": "USD",
                        "to": "EUR",
                        "bid": 0.81281,
                        "ask": 0.81281,
                        "price": 0.81281,
                        "timestamp": "2023-03-23T00:00:00Z"              
                    }
                ]""".replaceAll("\\s", "");
        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponseBody)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        List<ExchangeRate> response = currencyExchangeService.getExchangeRate("USD", "EUR");

        assertEquals(1, response.size());
        assertEquals("USD", response.get(0).getFrom());
        assertEquals("EUR", response.get(0).getTo());
        assertEquals(0.81281, response.get(0).getPrice(), 0.00001);
    }

    @Test
    void getExchangeRate_returnsClientError() throws IOException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("bad request"));

        Exception e = assertThrows(CurrencyExchangeServiceError.class, () -> {
            currencyExchangeService.getExchangeRate("USD", "EUR");
        });
        assertEquals("CurrencyExchangeServiceError Client error: bad request", e.getMessage());
    }

    @Test
    void getExchangeRate_returnsServerError() throws IOException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("The server is down, sorry!"));
        Exception e = assertThrows(CurrencyExchangeServiceError.class, () -> {
            currencyExchangeService.getExchangeRate("USD", "EUR");
        });
        assertEquals("CurrencyExchangeServiceError Server error: The server is down, sorry!", e.getMessage());
    }
}