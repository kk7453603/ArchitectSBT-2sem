import com.example.currencyprovider.service.CurrencyRateService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Реализация gRPC сервиса CurrencyRateService.
 * 
 * Этот класс обрабатывает входящие gRPC запросы и делегирует
 * бизнес-логику сервису CurrencyRateService.
 * 
 * @GrpcService автоматически регистрирует этот сервис в gRPC сервере
 * и управляет жизненным циклом через Spring.
 */
@GrpcService
public class CurrencyRateGrpcService extends CurrencyRateServiceGrpc.CurrencyRateServiceImplBase {

    /**
     * Логгер для записи информации о запросах и ошибках.
     */
    private static final Logger logger = LoggerFactory.getLogger(CurrencyRateGrpcService.class);

    /**
     * Сервис бизнес-логики для расчёта курса валюты.
     */
    private final CurrencyRateService currencyRateService;

    /**
     * Конструктор с внедрением зависимости.
     * 
     * @param currencyRateService сервис бизнес-логики
     */
    public CurrencyRateGrpcService(CurrencyRateService currencyRateService) {
        this.currencyRateService = currencyRateService;
    }

    /**
     * Обрабатывает запрос GetRate от клиента.
     * 
     * @param request пустой запрос (в соответствии с proto определением)
     * @param responseObserver объект для отправки ответа клиенту
     */
    @Override
    public void getRate(EmptyRequest request, StreamObserver<CurrencyRateResponse> responseObserver) {
        try {
            // Логируем получение запроса
            logger.info("Получен запрос GetRate от клиента");
            
            // Получаем данные курса от бизнес-сервиса
            CurrencyRateService.RateData rateData = currencyRateService.getCurrentRate();
            
            // Создаём ответ
            CurrencyRateResponse response = CurrencyRateResponse.newBuilder()
                    .setRate(rateData.getRate())
                    .setTimestamp(rateData.getTimestamp())
                    .build();
            
            // Отправляем ответ клиенту
            responseObserver.onNext(response);
            
            // Завершаем вызов успешно
            responseObserver.onCompleted();
            
            // Логируем успешную отправку ответа
            logger.info("Отправлен ответ: курс={:.2f}, timestamp={}",
                    rateData.getRate(), rateData.getTimestamp());
            
        } catch (Exception e) {
            // Логируем ошибку
            logger.error("Ошибка при обработке запроса GetRate: {}", e.getMessage(), e);
            
            // Отправляем ошибку клиенту с статусом INTERNAL
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Внутренняя ошибка сервера: " + e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        }
    }
}
