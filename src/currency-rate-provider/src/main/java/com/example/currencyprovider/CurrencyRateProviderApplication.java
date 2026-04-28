package com.example.currencyprovider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения Currency Rate Provider.
 * 
 * Это Spring Boot приложение, которое запускает gRPC сервер
 * для предоставления курса USDRUB с случайным отклонением.
 * 
 * @SpringBootApplication включает:
 * - @Configuration: конфигурация Spring
 * - @EnableAutoConfiguration: автоматическая настройка
 * - @ComponentScan: сканирование компонентов
 */
@SpringBootApplication
public class CurrencyRateProviderApplication {

    /**
     * Метод запуска приложения.
     * 
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        // Запуск Spring Boot приложения
        SpringApplication.run(CurrencyRateProviderApplication.class, args);
    }
}
