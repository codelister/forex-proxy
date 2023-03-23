package com.example.forexproxy.controllers;

import com.example.forexproxy.models.Currency;
import com.example.forexproxy.models.CurrencyConversionResult;
import com.example.forexproxy.services.CurrencyExchangeService;
import com.example.forexproxy.services.models.ExchangeRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/exchange-rates")
@Validated
public class CurrencyExchangeController {
    @Autowired
    private CurrencyExchangeService currencyExchangeService;
    @GetMapping
    public CurrencyConversionResult getExchangeRate(
            @RequestParam @Currency String fromCurrency,
            @RequestParam @Currency String toCurrency
    ) {
        List<ExchangeRate> response = currencyExchangeService.getExchangeRate(fromCurrency, toCurrency);
        ExchangeRate exchangeRate = response.get(0);
        return new CurrencyConversionResult(exchangeRate.getFrom(), exchangeRate.getTo(), exchangeRate.getPrice());
    }
}
