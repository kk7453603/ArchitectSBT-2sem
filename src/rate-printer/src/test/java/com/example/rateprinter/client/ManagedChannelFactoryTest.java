package com.example.rateprinter.client;

import io.grpc.ManagedChannel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ManagedChannelFactoryTest {

    @Test
    void createShouldBuildManagedChannel() {
        ManagedChannelFactory factory = new ManagedChannelFactory();

        ManagedChannel channel = factory.create("localhost", 9090);

        assertThat(channel).isNotNull();
        channel.shutdownNow();
    }
}
