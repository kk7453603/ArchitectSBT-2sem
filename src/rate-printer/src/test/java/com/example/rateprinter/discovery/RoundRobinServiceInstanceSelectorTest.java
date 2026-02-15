package com.example.rateprinter.discovery;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RoundRobinServiceInstanceSelectorTest {

    @Test
    void selectShouldReturnEmptyForEmptyList() {
        RoundRobinServiceInstanceSelector selector = new RoundRobinServiceInstanceSelector();

        Optional<ServiceInstance> selected = selector.select(List.of());

        assertThat(selected).isEmpty();
    }

    @Test
    void selectShouldCycleInstancesInRoundRobinOrder() {
        RoundRobinServiceInstanceSelector selector = new RoundRobinServiceInstanceSelector();
        ServiceInstance first = new DefaultServiceInstance("id-1", "currency-rate-provider", "localhost", 9091, false);
        ServiceInstance second = new DefaultServiceInstance("id-2", "currency-rate-provider", "localhost", 9092, false);
        ServiceInstance third = new DefaultServiceInstance("id-3", "currency-rate-provider", "localhost", 9093, false);
        List<ServiceInstance> instances = List.of(first, second, third);

        assertThat(selector.select(instances)).contains(first);
        assertThat(selector.select(instances)).contains(second);
        assertThat(selector.select(instances)).contains(third);
        assertThat(selector.select(instances)).contains(first);
    }
}
