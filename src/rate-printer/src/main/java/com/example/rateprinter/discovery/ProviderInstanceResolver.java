package com.example.rateprinter.discovery;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Резолвер инстансов сервиса-провайдера через Spring DiscoveryClient.
 */
@Component
public class ProviderInstanceResolver {

    private final DiscoveryClient discoveryClient;

    public ProviderInstanceResolver(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    public List<ServiceInstance> resolveInstances(String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        if (instances == null || instances.isEmpty()) {
            return Collections.emptyList();
        }
        return List.copyOf(instances);
    }
}
