package com.example.currencyprovider.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CurrencyRateServiceTest {

    @Test
    void getCurrentRateShouldReturnValueInExpectedRange() {
        CurrencyRateService service = new CurrencyRateService();

        for (int i = 0; i < 100; i++) {
            CurrencyRateService.RateData rateData = service.getCurrentRate();
            assertThat(rateData.getRate()).isBetween(87.0, 98.0);
        }
    }

    @Test
    void getCurrentRateShouldReturnPositiveTimestamp() {
        CurrencyRateService service = new CurrencyRateService();

        CurrencyRateService.RateData rateData = service.getCurrentRate();

        assertThat(rateData.getTimestamp()).isPositive();
    }
}
