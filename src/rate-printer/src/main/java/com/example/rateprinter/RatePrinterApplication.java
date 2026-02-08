import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Главный класс приложения Rate Printer.
 * Включает планировщик для периодических запросов к gRPC серверу.
 */
@SpringBootApplication
@EnableScheduling
public class RatePrinterApplication {

    /**
     * Точка входа в приложение.
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(RatePrinterApplication.class, args);
    }
}
