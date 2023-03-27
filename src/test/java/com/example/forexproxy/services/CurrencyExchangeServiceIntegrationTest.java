package com.example.forexproxy.services;

import com.example.forexproxy.ForexProxyApplication;
import com.example.forexproxy.services.models.ExchangeRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Description;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ForexProxyApplication.class)
public class CurrencyExchangeServiceIntegrationTest {

    @Autowired
    private CurrencyExchangeService service;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    public void setup() {
        cacheManager.getCache("exchangeRates").clear();
    }

    @Test
    public void verifyCacheIsUsed() {
        assertTrue(getCachedExchangeRates().isEmpty());
        service.getAllExchangeRates();

        assertFalse(getCachedExchangeRates().isEmpty());
        service.getAllExchangeRates();
    }

    @Test
    @Description("Verify the cache is evicted after the configured time")
    public void verifyCacheIsEvicted() throws InterruptedException {
        assertTrue(getCachedExchangeRates().isEmpty());
        service.getAllExchangeRates();
        assertFalse(getCachedExchangeRates().isEmpty());
        Thread.sleep(1000);
        assertTrue(getCachedExchangeRates().isEmpty());
    }

    private List<ExchangeRate> getCachedExchangeRates() {
        Cache cache = cacheManager.getCache("exchangeRates");
        try {
            List<ExchangeRate> cachedExchangeRates = cache.get("exchangeRatesKey", List.class);
            if (cachedExchangeRates == null) {
                return List.of();
            } else {
                return cachedExchangeRates;
            }
        } catch (NullPointerException e) {
            return List.of();
        }
    }
}