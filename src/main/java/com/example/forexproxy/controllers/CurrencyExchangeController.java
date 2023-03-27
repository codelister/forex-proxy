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
        if (fromCurrency.equals(toCurrency)) {
            // Removing this will simplify the code, but potentially unnecessarily eat at our API limit
            // 400 might be more appropriate since this is meaningless
            return new CurrencyConversionResult(fromCurrency, toCurrency, 1.0);
        }
        List<ExchangeRate> response = currencyExchangeService.getAllExchangeRates();
        return response.stream()
                .filter(rate ->
                        (rate.getFrom().equals(fromCurrency) && rate.getTo().equals(toCurrency)) || (rate.getFrom().equals(toCurrency) && rate.getTo().equals(fromCurrency)))
                .findFirst()
                .map(e -> new CurrencyConversionResult(e.getFrom(), e.getTo(), e.getPrice()))
                .orElseThrow(() -> new RuntimeException("Exchange rate not found"));
    }
}
