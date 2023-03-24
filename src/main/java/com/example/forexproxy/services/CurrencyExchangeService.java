package com.example.forexproxy.services;

import com.example.forexproxy.services.models.CurrencyExchangeServiceError;
import com.example.forexproxy.services.models.ExchangeRate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.forexproxy.config.Configuration.ALLOWED_CURRENCIES;
import static com.example.forexproxy.config.Configuration.ALLOWED_CURRENCIES_PAIR_COUNT;

@Service
public class CurrencyExchangeService {
    private String baseUri;
    private String token;
    private WebClient webClient;

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

    @Cacheable("exchangeRates")
    public List<ExchangeRate> getAllExchangeRates() {
        String queryParams = ALLOWED_CURRENCIES.stream()
                .flatMap(c1 -> ALLOWED_CURRENCIES.stream()
                        .filter(c2 -> c2.compareTo(c1) > 0)
                        .map(c2 -> "pair=" + c1 + c2))
                .collect(Collectors.joining("&"));

        String uri = UriComponentsBuilder
                .fromPath("rates")
                .query(queryParams)
                .build()
                .encode()
                .toUriString();
        try {
            List<ExchangeRate> response = webClient.get()
                    .uri(uri)
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
            if (response.size() != ALLOWED_CURRENCIES_PAIR_COUNT) {
                String errorMessage = "CurrencyExchangeServiceError Server error: " + "Invalid response from currency exchange service";
                throw new CurrencyExchangeServiceError(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return response;
        }
        catch (CurrencyExchangeServiceError e){
            throw e;
        }
        catch (Exception e) {
            String errorMessage = "CurrencyExchangeServiceError Unknown error: " + e.getMessage();
            throw new CurrencyExchangeServiceError(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

