import com.example.currencyprovider.grpc.CurrencyRateResponse;
import com.example.currencyprovider.grpc.CurrencyRateServiceGrpc;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Сервис для периодического получения и вывода курса валют.
 * Запрашивает курс USDRUB каждые 5 секунд через gRPC.
 */
@Service
public class RatePrinterService {

    private static final Logger logger = LoggerFactory.getLogger(RatePrinterService.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.of("UTC"));

    private final CurrencyRateServiceGrpc.CurrencyRateServiceBlockingStub blockingStub;

    /**
     * Конструктор с внедрением зависимости gRPC stub.
     * @param blockingStub stub для синхронных вызовов gRPC
     */
    @Autowired
    public RatePrinterService(CurrencyRateServiceGrpc.CurrencyRateServiceBlockingStub blockingStub) {
        this.blockingStub = blockingStub;
    }

    /**
     * Периодический метод для получения и вывода курса валют.
     * Вызывается каждые 5 секунд.
     */
    @Scheduled(fixedRate = 5000)
    public void printRate() {
        try {
            // Создаем пустой запрос
            com.example.currencyprovider.grpc.EmptyRequest request = 
                    com.example.currencyprovider.grpc.EmptyRequest.getDefaultInstance();

            // Отправляем запрос и получаем ответ
            CurrencyRateResponse response = blockingStub.getRate(request);

            // Форматируем timestamp
            String formattedTime = FORMATTER.format(Instant.ofEpochMilli(response.getTimestamp()));

            // Форматируем и выводим курс
            String logMessage = String.format("USD/RUB: %.2f (timestamp: %s)", 
                    response.getRate(), formattedTime);
            
            logger.info("[RatePrinter] {}", logMessage);

        } catch (StatusRuntimeException e) {
            logger.error("[RatePrinter] Ошибка подключения к gRPC серверу: {}", e.getStatus(), e);
        } catch (Exception e) {
            logger.error("[RatePrinter] Непредвиденная ошибка: {}", e.getMessage(), e);
        }
    }
}
