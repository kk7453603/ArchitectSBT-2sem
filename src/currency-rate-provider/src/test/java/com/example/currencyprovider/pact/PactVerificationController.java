package com.example.currencyprovider.pact;

import com.example.currencyprovider.service.CurrencyRateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PactVerificationController {

    private final CurrencyRateService currencyRateService;

    public PactVerificationController(CurrencyRateService currencyRateService) {
        this.currencyRateService = currencyRateService;
    }

    @GetMapping("/rate")
    public Map<String, Object> getRate() {
        CurrencyRateService.RateData rateData = currencyRateService.getCurrentRate();
        return Map.of(
                "rate", rateData.getRate(),
                "timestamp", rateData.getTimestamp()
        );
    }
}
