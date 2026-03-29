package com.example.rateprinter.client;

import com.example.currencyprovider.grpc.CurrencyRateServiceGrpc;
import io.grpc.ManagedChannel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CurrencyRateBlockingStubFactoryTest {

    @Mock
    private ManagedChannel managedChannel;

    @Test
    void createShouldBuildBlockingStub() {
        CurrencyRateBlockingStubFactory factory = new CurrencyRateBlockingStubFactory();

        CurrencyRateServiceGrpc.CurrencyRateServiceBlockingStub stub = factory.create(managedChannel, 1500L);

        assertThat(stub).isNotNull();
    }
}
