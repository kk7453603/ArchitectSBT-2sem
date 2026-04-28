package com.example.rateprinter.client;

import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Фабрика gRPC каналов.
 */
@Component
public class ManagedChannelFactory {

    private final List<ClientInterceptor> interceptors;

    public ManagedChannelFactory(List<ClientInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public ManagedChannel create(String host, int port) {
        return ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .intercept(interceptors)
                .build();
    }
}
