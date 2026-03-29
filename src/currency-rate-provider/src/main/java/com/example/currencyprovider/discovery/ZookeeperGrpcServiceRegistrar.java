package com.example.currencyprovider.discovery;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.cloud.zookeeper.serviceregistry.ZookeeperRegistration;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Явная регистрация gRPC-only сервиса в Service Registry.
 * Для non-web приложения авто-регистрация Spring Cloud может не сработать,
 * поэтому регистрируемся вручную после старта контекста.
 */
@Component
public class ZookeeperGrpcServiceRegistrar {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperGrpcServiceRegistrar.class);

    private final ServiceRegistry<ZookeeperRegistration> serviceRegistry;
    private final ZookeeperRegistration registration;
    private volatile boolean registered;

    public ZookeeperGrpcServiceRegistrar(
            ServiceRegistry<ZookeeperRegistration> serviceRegistry,
            ZookeeperRegistration registration
    ) {
        this.serviceRegistry = serviceRegistry;
        this.registration = registration;
    }

    @EventListener(ApplicationReadyEvent.class)
    public synchronized void register() {
        if (registered) {
            return;
        }
        serviceRegistry.register(registration);
        registered = true;
        logger.info("Service registered in ZooKeeper: serviceId={}", registration.getServiceId());
    }

    @PreDestroy
    public synchronized void deregister() {
        if (!registered) {
            return;
        }
        serviceRegistry.deregister(registration);
        registered = false;
        logger.info("Service deregistered from ZooKeeper: serviceId={}", registration.getServiceId());
    }
}
