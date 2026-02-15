package com.example.rateprinter.discovery;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProviderInstanceResolverTest {

    @Mock
    private DiscoveryClient discoveryClient;

    @Test
    void resolveInstancesShouldReturnEmptyListWhenDiscoveryReturnsNull() {
        when(discoveryClient.getInstances("currency-rate-provider")).thenReturn(null);
        ProviderInstanceResolver resolver = new ProviderInstanceResolver(discoveryClient);

        List<ServiceInstance> instances = resolver.resolveInstances("currency-rate-provider");

        assertThat(instances).isEmpty();
    }

    @Test
    void resolveInstancesShouldReturnDiscoveredInstances() {
        ServiceInstance first = new DefaultServiceInstance("id-1", "currency-rate-provider", "localhost", 9091, false);
        ServiceInstance second = new DefaultServiceInstance("id-2", "currency-rate-provider", "localhost", 9092, false);
        when(discoveryClient.getInstances("currency-rate-provider")).thenReturn(List.of(first, second));
        ProviderInstanceResolver resolver = new ProviderInstanceResolver(discoveryClient);

        List<ServiceInstance> instances = resolver.resolveInstances("currency-rate-provider");

        assertThat(instances).containsExactly(first, second);
    }
}
