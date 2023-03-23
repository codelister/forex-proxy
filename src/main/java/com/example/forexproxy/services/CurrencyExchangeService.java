package com.example.forexproxy.services;

import com.example.forexproxy.services.models.CurrencyExchangeServiceError;
import com.example.forexproxy.services.models.ExchangeRate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class CurrencyExchangeService {
    private String baseUri;
    private String token;
    private WebClient webClient;
    // TODO: Add caching if api is too flaky
    public CurrencyExchangeService(
            @Value("${currency.exchange.service.uri}") String baseUri,
            @Value("${currency.exchange.service.token}") String token
    ) {
        this.baseUri = baseUri;
        this.token = token;
        this.webClient = WebClient.builder()
                .baseUrl(this.baseUri)
                .defaultCookie("cookieKey", "cookieValue")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("token", this.token)
                .build();
    }

    public List<ExchangeRate> getExchangeRate(String fromCurrency, String toCurrency) {
        List<ExchangeRate> response = webClient.get()
                .uri("/rates?pair={currency_pair_0}", fromCurrency + toCurrency)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp -> {
                    Mono<String> responseBodyMono = resp.bodyToMono(String.class);
                    return responseBodyMono.flatMap(responseBody -> {
                        String errorMessage = "CurrencyExchangeServiceError Client error: " + responseBody;
                        return Mono.error(new CurrencyExchangeServiceError(errorMessage, resp.statusCode()));
                    });
                })
                .onStatus(HttpStatusCode::is5xxServerError, resp -> {
                    Mono<String> responseBodyMono = resp.bodyToMono(String.class);
                    return responseBodyMono.flatMap(responseBody -> {
                        String errorMessage = "CurrencyExchangeServiceError Server error: " + responseBody;
                        return Mono.error(new CurrencyExchangeServiceError(errorMessage, resp.statusCode()));
                    });
                })
                .bodyToFlux(ExchangeRate.class)
                .collectList()
                .block();
        return response;
    }
}

