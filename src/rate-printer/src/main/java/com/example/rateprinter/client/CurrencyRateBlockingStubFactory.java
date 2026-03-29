package com.example.rateprinter.client;

import com.example.currencyprovider.grpc.CurrencyRateServiceGrpc;
import io.grpc.ManagedChannel;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Фабрика blocking stub для вызовов CurrencyRateService.
 */
@Component
public class CurrencyRateBlockingStubFactory {

    public CurrencyRateServiceGrpc.CurrencyRateServiceBlockingStub create(ManagedChannel channel, long timeoutMs) {
        return CurrencyRateServiceGrpc.newBlockingStub(channel)
                .withDeadlineAfter(timeoutMs, TimeUnit.MILLISECONDS);
    }
}
