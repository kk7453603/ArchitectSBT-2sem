package com.example.rateprinter.client;

import com.example.currencyprovider.grpc.CurrencyRateResponse;
import com.example.currencyprovider.grpc.CurrencyRateServiceGrpc;
import com.example.currencyprovider.grpc.EmptyRequest;
import io.grpc.ManagedChannel;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * gRPC клиент для запроса курса у конкретного инстанса provider-сервиса.
 */
@Component
public class GrpcCurrencyRateClient {

    private final ManagedChannelFactory channelFactory;
    private final CurrencyRateBlockingStubFactory stubFactory;

    public GrpcCurrencyRateClient(
            ManagedChannelFactory channelFactory,
            CurrencyRateBlockingStubFactory stubFactory
    ) {
        this.channelFactory = channelFactory;
        this.stubFactory = stubFactory;
    }

    public CurrencyRateResponse getRate(ServiceInstance serviceInstance, long timeoutMs) {
        ManagedChannel channel = channelFactory.create(serviceInstance.getHost(), serviceInstance.getPort());
        try {
            CurrencyRateServiceGrpc.CurrencyRateServiceBlockingStub blockingStub = stubFactory.create(channel, timeoutMs);
            return blockingStub.getRate(EmptyRequest.getDefaultInstance());
        } finally {
            shutdownChannel(channel);
        }
    }

    void shutdownChannel(ManagedChannel channel) {
        channel.shutdown();
        try {
            if (!channel.awaitTermination(1, TimeUnit.SECONDS)) {
                channel.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            channel.shutdownNow();
        }
    }
}
