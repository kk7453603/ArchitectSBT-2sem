package com.example.rateprinter.client;

import com.example.currencyprovider.grpc.CurrencyRateResponse;
import com.example.currencyprovider.grpc.CurrencyRateServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrpcCurrencyRateClientTest {

    @Mock
    private ManagedChannelFactory channelFactory;

    @Mock
    private CurrencyRateBlockingStubFactory stubFactory;

    @Mock
    private ManagedChannel managedChannel;

    @Mock
    private CurrencyRateServiceGrpc.CurrencyRateServiceBlockingStub blockingStub;

    @Test
    void getRateShouldReturnResponseAndCloseChannel() throws Exception {
        ServiceInstance serviceInstance = new DefaultServiceInstance(
                "id-1",
                "currency-rate-provider",
                "localhost",
                9091,
                false
        );
        CurrencyRateResponse expectedResponse = CurrencyRateResponse.newBuilder()
                .setRate(93.15)
                .setTimestamp(1_700_000_000_000L)
                .build();

        when(channelFactory.create("localhost", 9091)).thenReturn(managedChannel);
        when(stubFactory.create(managedChannel, 1500L)).thenReturn(blockingStub);
        when(blockingStub.getRate(any())).thenReturn(expectedResponse);
        when(managedChannel.awaitTermination(eq(1L), eq(TimeUnit.SECONDS))).thenReturn(true);

        GrpcCurrencyRateClient client = new GrpcCurrencyRateClient(channelFactory, stubFactory);
        CurrencyRateResponse actualResponse = client.getRate(serviceInstance, 1500L);

        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(managedChannel).shutdown();
        verify(managedChannel).awaitTermination(1L, TimeUnit.SECONDS);
    }

    @Test
    void getRateShouldPropagateGrpcExceptionAndStillCloseChannel() throws Exception {
        ServiceInstance serviceInstance = new DefaultServiceInstance(
                "id-1",
                "currency-rate-provider",
                "localhost",
                9091,
                false
        );
        StatusRuntimeException expectedException = new StatusRuntimeException(Status.DEADLINE_EXCEEDED);

        when(channelFactory.create("localhost", 9091)).thenReturn(managedChannel);
        when(stubFactory.create(managedChannel, 1500L)).thenReturn(blockingStub);
        when(blockingStub.getRate(any())).thenThrow(expectedException);
        when(managedChannel.awaitTermination(eq(1L), eq(TimeUnit.SECONDS))).thenReturn(false);

        GrpcCurrencyRateClient client = new GrpcCurrencyRateClient(channelFactory, stubFactory);

        assertThatThrownBy(() -> client.getRate(serviceInstance, 1500L))
                .isInstanceOf(StatusRuntimeException.class)
                .hasMessageContaining(Status.DEADLINE_EXCEEDED.getCode().name());
        verify(managedChannel).shutdown();
        verify(managedChannel).awaitTermination(1L, TimeUnit.SECONDS);
        verify(managedChannel).shutdownNow();
    }
}
