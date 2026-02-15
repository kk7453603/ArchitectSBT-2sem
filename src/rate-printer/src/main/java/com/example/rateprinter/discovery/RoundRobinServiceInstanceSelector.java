package com.example.rateprinter.discovery;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Базовый round-robin селектор инстансов.
 */
@Component
public class RoundRobinServiceInstanceSelector {

    private final AtomicInteger index = new AtomicInteger(0);

    public Optional<ServiceInstance> select(List<ServiceInstance> instances) {
        if (instances == null || instances.isEmpty()) {
            return Optional.empty();
        }
        int selectedIndex = Math.floorMod(index.getAndIncrement(), instances.size());
        return Optional.of(instances.get(selectedIndex));
    }
}
