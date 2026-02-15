package com.example.rateprinter.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Component;

/**
 * Фабрика gRPC каналов.
 */
@Component
public class ManagedChannelFactory {

    public ManagedChannel create(String host, int port) {
        return ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
    }
}
