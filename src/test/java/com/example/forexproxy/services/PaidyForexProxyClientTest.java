package com.example.forexproxy.services;

import com.example.forexproxy.services.clients.PaidyForexProxyClient;
import com.example.forexproxy.services.models.CurrencyExchangeServiceError;
import com.example.forexproxy.services.models.ExchangeRate;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaidyForexProxyClientTest {

    private MockWebServer mockWebServer;
    private PaidyForexProxyClient client;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        client = new PaidyForexProxyClient(baseUrl, "test-token");
    }

    @Test
    void getExchangeRate_returnsExpectedResponse() {
        ObjectMapper objectMapper = new ObjectMapper();
        List<ExchangeRate> expectedResponse = new ArrayList<>();
        ExchangeRate fakeRate = new ExchangeRate("USD", "EUR", 0.81281,0.81281,0.81281,"2023-03-23T00:00:00Z");
        int supportedCurrencyCount = 36;
        for (int i = 0; i < supportedCurrencyCount; i++) {
            expectedResponse.add(fakeRate);
        }
        String mockResponseBody = objectMapper.valueToTree(expectedResponse).toString();

        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponseBody)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        List<ExchangeRate> response = client.getAllExchangeRates();

        assertEquals(supportedCurrencyCount, response.size());
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
            client.getAllExchangeRates();
        });
        assertEquals("CurrencyExchangeServiceError Client error: bad request", e.getMessage());
    }

    @Test
    void getExchangeRate_returnsServerError() throws IOException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("The server is down, sorry!"));
        Exception e = assertThrows(CurrencyExchangeServiceError.class, () -> {
            client.getAllExchangeRates();
        });
        assertEquals("CurrencyExchangeServiceError Server error: The server is down, sorry!", e.getMessage());
    }

}