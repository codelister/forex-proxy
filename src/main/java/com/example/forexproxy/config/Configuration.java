package com.example.forexproxy.config;

import java.util.Set;

public class Configuration {
    //TODO: Read from config file
    public static final Set<String> ALLOWED_CURRENCIES = Set.of("AUD", "CAD", "CHF", "EUR", "GBP", "NZD", "JPY", "SGD", "USD");
    //TODO: Calculate this value
    public static final int ALLOWED_CURRENCIES_PAIR_COUNT = 36;
}