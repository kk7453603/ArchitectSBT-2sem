package com.example.rateprinter.service;

import com.example.currencyprovider.grpc.CurrencyRateResponse;
import com.example.rateprinter.client.GrpcCurrencyRateClient;
import com.example.rateprinter.discovery.ProviderInstanceResolver;
import com.example.rateprinter.discovery.RoundRobinServiceInstanceSelector;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для периодического получения и вывода курса валют.
 * Получает инстансы provider через service discovery и выбирает target по round-robin.
 */
@Service
public class RatePrinterService {

    private static final Logger logger = LoggerFactory.getLogger(RatePrinterService.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.of("UTC"));

    private final ProviderInstanceResolver providerInstanceResolver;
    private final RoundRobinServiceInstanceSelector serviceInstanceSelector;
    private final GrpcCurrencyRateClient grpcCurrencyRateClient;
    private final String providerServiceName;
    private final long rpcTimeoutMs;

    public RatePrinterService(
            ProviderInstanceResolver providerInstanceResolver,
            RoundRobinServiceInstanceSelector serviceInstanceSelector,
            GrpcCurrencyRateClient grpcCurrencyRateClient,
            @Value("${rate.provider.service-name}") String providerServiceName,
            @Value("${rate.provider.rpc-timeout-ms}") long rpcTimeoutMs
    ) {
        this.providerInstanceResolver = providerInstanceResolver;
        this.serviceInstanceSelector = serviceInstanceSelector;
        this.grpcCurrencyRateClient = grpcCurrencyRateClient;
        this.providerServiceName = providerServiceName;
        this.rpcTimeoutMs = rpcTimeoutMs;
    }

    /**
     * Периодический метод для получения и вывода курса валют.
     */
    @Scheduled(fixedRateString = "${rate.printer.interval-ms}")
    public void printRate() {
        try {
            List<ServiceInstance> instances = providerInstanceResolver.resolveInstances(providerServiceName);
            Optional<ServiceInstance> selectedInstance = serviceInstanceSelector.select(instances);

            if (selectedInstance.isEmpty()) {
                logger.warn("[RatePrinter] Нет доступных инстансов сервиса '{}'", providerServiceName);
                return;
            }

            ServiceInstance serviceInstance = selectedInstance.get();
            CurrencyRateResponse response = grpcCurrencyRateClient.getRate(serviceInstance, rpcTimeoutMs);
            String formattedTime = FORMATTER.format(Instant.ofEpochMilli(response.getTimestamp()));

            logger.info(
                    "[RatePrinter] USD/RUB: {} (timestamp: {}, provider: {}:{})",
                    String.format("%.2f", response.getRate()),
                    formattedTime,
                    serviceInstance.getHost(),
                    serviceInstance.getPort()
            );
        } catch (StatusRuntimeException e) {
            logger.error("[RatePrinter] Ошибка gRPC вызова: {}", e.getStatus(), e);
        } catch (Exception e) {
            logger.error("[RatePrinter] Непредвиденная ошибка: {}", e.getMessage(), e);
        }
    }
}
