package com.example.currencyprovider.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Сервис для расчёта курса USDRUB с случайным отклонением.
 * 
 * Этот сервис реализует бизнес-логику генерации курса валюты
 * с учётом случайных колебаний в заданном диапазоне.
 */
@Service
public class CurrencyRateService {

    /**
     * Нижняя граница базового курса USDRUB.
     */
    private static final double BASE_RATE_MIN = 90.0;

    /**
     * Верхняя граница базового курса USDRUB.
     */
    private static final double BASE_RATE_MAX = 95.0;

    /**
     * Максимальное отклонение от базового курса.
     */
    private static final double MAX_DEVIATION = 3.0;

    /**
     * Логгер для записи информации о запросах.
     */
    private static final Logger logger = LoggerFactory.getLogger(CurrencyRateService.class);

    /**
     * Возвращает текущий курс USDRUB с случайным отклонением.
     * 
     * Алгоритм:
     * 1. Выбирается базовый курс в диапазоне 90-95
     * 2. Добавляется случайное отклонение в диапазоне [-3, +3]
     * 3. Возвращается итоговое значение с текущей временной меткой
     * 
     * @return объект RateData с курсом и временной меткой
     */
    public RateData getCurrentRate() {
        // Генерируем базовый курс в диапазоне [90, 95)
        double baseRate = BASE_RATE_MIN + (BASE_RATE_MAX - BASE_RATE_MIN) * ThreadLocalRandom.current().nextDouble();
        
        // Генерируем случайное отклонение в диапазоне [-3, +3]
        double deviation = (ThreadLocalRandom.current().nextDouble() * 2 - 1) * MAX_DEVIATION;
        
        // Вычисляем итоговый курс
        double finalRate = baseRate + deviation;
        
        // Получаем текущую временную метку
        long timestamp = System.currentTimeMillis();
        
        // Логируем информацию о сгенерированном курсе
        logger.info(
                "Сгенерирован курс USDRUB: {} (базовый: {}, отклонение: {})",
                String.format("%.2f", finalRate),
                String.format("%.2f", baseRate),
                String.format("%.2f", deviation)
        );
        
        return new RateData(finalRate, timestamp);
    }

    /**
     * Внутренний класс для хранения данных о курсе валюты.
     */
    public static class RateData {
        private final double rate;
        private final long timestamp;

        /**
         * Конструктор для создания объекта с курсом и временной меткой.
         * 
         * @param rate курс валюты
         * @param timestamp временная метка в миллисекундах
         */
        public RateData(double rate, long timestamp) {
            this.rate = rate;
            this.timestamp = timestamp;
        }

        /**
         * Возвращает курс валюты.
         * 
         * @return курс USDRUB
         */
        public double getRate() {
            return rate;
        }

        /**
         * Возвращает временную метку.
         * 
         * @return временная метка в миллисекундах
         */
        public long getTimestamp() {
            return timestamp;
        }

        @Override
        public String toString() {
            return String.format("RateData{rate=%.2f, timestamp=%d}", rate, timestamp);
        }
    }
}
