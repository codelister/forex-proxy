package com.example.forexproxy.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import static javax.management.timer.Timer.ONE_MINUTE;

@EnableCaching
@EnableScheduling
@Configuration
public class CacheConfig {

    public static final String CACHE_NAME = "exchangeRates";

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(CACHE_NAME);
    }

    @Scheduled(fixedRate = ONE_MINUTE * 4)
    public void evictCache() {
        cacheManager().getCache(CACHE_NAME).clear();
    }
}
