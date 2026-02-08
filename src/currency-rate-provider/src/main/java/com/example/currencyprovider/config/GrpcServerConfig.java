import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация gRPC сервера.
 * 
 * Этот класс является конфигурационным классом Spring,
 * который автоматически настраивает gRPC сервер с помощью
 * библиотеки grpc-spring-boot-starter.
 * 
 * Основные настройки (порт 9090) задаются в файле application.properties.
 * Конфигурация автоматически:
 * - Сканирует бины с аннотацией @GrpcService
 * - Регистрирует их в gRPC сервере
 * - Управляет жизненным циклом сервера
 */
@Configuration
public class GrpcServerConfig {

    /**
     * Примечание: Дополнительная конфигурация не требуется,
     * так как grpc-spring-boot-starter автоматически
     * настраивает сервер на основе properties.
     * 
     * Основные настройки в application.properties:
     * - grpc.server.port=9090
     */
}
