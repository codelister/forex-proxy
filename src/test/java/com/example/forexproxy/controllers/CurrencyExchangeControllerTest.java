package com.example.forexproxy.controllers;


import com.example.forexproxy.models.CurrencyConversionResult;
import com.example.forexproxy.models.ErrorResponse;
import com.example.forexproxy.services.CurrencyExchangeService;
import com.example.forexproxy.services.models.ExchangeRate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CurrencyExchangeController.class)
class CurrencyExchangeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyExchangeService currencyExchangeService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Set<String> ALLOWED_CURRENCIES = Set.of("AUD", "CAD", "CHF", "EUR", "GBP", "NZD", "JPY", "SGD", "USD");

    @DisplayName("Test currency pair combinations")
    @ParameterizedTest(name = "Exchange rate between {0} and {1}")
    @MethodSource("generateCurrencyPairs")
    void whenValidInput_thenReturns200WithJson(String input1, String input2) throws Exception {
        when(currencyExchangeService.getExchangeRate(input1, input2)).thenReturn(List.of(new ExchangeRate(input1, input2, 1.0, 1.0, 1.0,"")));
        mockMvc.perform(get("/api/exchange-rates?fromCurrency={input1}&toCurrency={input2}", input1, input2))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String expected = objectMapper.writeValueAsString(new CurrencyConversionResult(input1, input2, 1.0));
                    String actual = result.getResponse().getContentAsString();
                    assert expected.equals(actual);
                });
    }

    @Test
    void whenInvalidInput_thenReturns400WithJson() throws Exception {
        String input1 = "USD";
        String input2 = "BADBADNOTGOOD";
        when(currencyExchangeService.getExchangeRate(input1, input2)).thenReturn(List.of(new ExchangeRate(input1, input2, 1.0, 1.0, 1.0,"")));
        mockMvc.perform(get("/api/exchange-rates?fromCurrency={input1}&toCurrency={input2}", input1, input2))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String expected = objectMapper.writeValueAsString(new ErrorResponse(HttpStatusCode.valueOf(400), "getExchangeRate.toCurrency: Invalid currency"));
                    String actual = result.getResponse().getContentAsString();
                    assert actual.equals(expected);
                });
    }

    @Test
    void whenException_thenReturns500WithJson() throws Exception {
        String input1 = "USD";
        String input2 = "CAD";
        when(currencyExchangeService.getExchangeRate(input1, input2)).thenThrow(new RuntimeException());
        mockMvc.perform(get("/api/exchange-rates?fromCurrency={input1}&toCurrency={input2}", input1, input2))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> {
                    String expected = objectMapper.writeValueAsString(new ErrorResponse(HttpStatusCode.valueOf(500), "An unexpected error has occurred"));
                    String actual = result.getResponse().getContentAsString();
                    assert actual.equals(expected);
                });
    }

    private static Stream<Arguments> generateCurrencyPairs() {
        return ALLOWED_CURRENCIES.stream()
                .flatMap(currency1 -> ALLOWED_CURRENCIES.stream()
                        .filter(currency2 -> !currency1.equals(currency2))
                        .map(currency2 -> Arguments.of(currency1, currency2)));
    }
}