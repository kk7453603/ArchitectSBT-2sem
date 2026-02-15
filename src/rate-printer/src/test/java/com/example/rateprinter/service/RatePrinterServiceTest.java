package com.example.rateprinter.service;

import com.example.currencyprovider.grpc.CurrencyRateResponse;
import com.example.rateprinter.client.GrpcCurrencyRateClient;
import com.example.rateprinter.discovery.ProviderInstanceResolver;
import com.example.rateprinter.discovery.RoundRobinServiceInstanceSelector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatePrinterServiceTest {

    @Mock
    private ProviderInstanceResolver providerInstanceResolver;

    @Mock
    private RoundRobinServiceInstanceSelector serviceInstanceSelector;

    @Mock
    private GrpcCurrencyRateClient grpcCurrencyRateClient;

    @Test
    void printRateShouldSkipGrpcCallWhenNoInstancesFound() {
        when(providerInstanceResolver.resolveInstances("currency-rate-provider")).thenReturn(List.of());
        when(serviceInstanceSelector.select(List.of())).thenReturn(Optional.empty());
        RatePrinterService service = new RatePrinterService(
                providerInstanceResolver,
                serviceInstanceSelector,
                grpcCurrencyRateClient,
                "currency-rate-provider",
                1500L
        );

        service.printRate();

        verify(providerInstanceResolver).resolveInstances("currency-rate-provider");
        verify(serviceInstanceSelector).select(List.of());
        verify(grpcCurrencyRateClient, never()).getRate(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    void printRateShouldInvokeGrpcCallWhenInstanceIsSelected() {
        ServiceInstance selected = new DefaultServiceInstance(
                "id-1",
                "currency-rate-provider",
                "localhost",
                9091,
                false
        );
        List<ServiceInstance> instances = List.of(selected);
        CurrencyRateResponse response = CurrencyRateResponse.newBuilder()
                .setRate(93.01)
                .setTimestamp(1_700_000_000_000L)
                .build();

        when(providerInstanceResolver.resolveInstances("currency-rate-provider")).thenReturn(instances);
        when(serviceInstanceSelector.select(instances)).thenReturn(Optional.of(selected));
        when(grpcCurrencyRateClient.getRate(selected, 1500L)).thenReturn(response);
        RatePrinterService service = new RatePrinterService(
                providerInstanceResolver,
                serviceInstanceSelector,
                grpcCurrencyRateClient,
                "currency-rate-provider",
                1500L
        );

        service.printRate();

        verify(providerInstanceResolver).resolveInstances("currency-rate-provider");
        verify(serviceInstanceSelector).select(instances);
        verify(grpcCurrencyRateClient).getRate(selected, 1500L);
    }
}
