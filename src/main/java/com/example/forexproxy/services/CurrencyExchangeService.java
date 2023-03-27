package com.example.forexproxy.services;

import com.example.forexproxy.services.clients.ForexProxyClient;
import com.example.forexproxy.services.models.ExchangeRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrencyExchangeService {

    @Autowired
    private ForexProxyClient client;

    public CurrencyExchangeService(ForexProxyClient client) {
        this.client = client;
    }

    @Cacheable(value = "exchangeRates", key = "'exchangeRatesKey'")
    public List<ExchangeRate> getAllExchangeRates() {
        return client.getAllExchangeRates();
    }
}

