package com.example.forexproxy.services.clients;

import com.example.forexproxy.services.models.ExchangeRate;

import java.util.List;

public interface ForexProxyClient {
    List<ExchangeRate> getAllExchangeRates();

}