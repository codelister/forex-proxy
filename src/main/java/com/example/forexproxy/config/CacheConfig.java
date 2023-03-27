package com.example.forexproxy.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


@EnableCaching
@EnableScheduling
@Configuration
public class CacheConfig {

    public static final String CACHE_NAME = "exchangeRates";

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(CACHE_NAME);
    }

    @Scheduled(fixedRateString = "${currency.exchange.service.cache.evictionTime}")
    public void evictCache() {
        cacheManager().getCache(CACHE_NAME).clear();
    }
}
