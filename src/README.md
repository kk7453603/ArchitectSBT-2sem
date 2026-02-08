# Currency Rate Services

Два Spring Boot микросервиса с gRPC коммуникацией для получения и отображения курса валют.

## Описание проекта

Проект демонстрирует архитектуру взаимодействия микросервисов через протокол gRPC:
- **Currency Rate Provider** — gRPC сервер, который генерирует и предоставляет курс валют (USD/RUB)
- **Rate Printer** — gRPC клиент, который подключается к серверу и выводит полученный курс в консоль с периодичностью 5 секунд

## Технологии

- **Spring Boot 3.x** — основной фреймворк для обоих сервисов
- **Java 17** — язык программирования
- **gRPC** — высокопроизводительный RPC-фреймворк для межсервисного взаимодействия
- **Maven** — система сборки проекта
- **Protocol Buffers** — формат сериализации данных для gRPC

## Архитектура

```
┌─────────────────────────────────────────────────────────────────┐
│                        Architecture                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌──────────────────────┐         ┌──────────────────────┐    │
│   │ Currency Rate        │         │ Rate Printer         │    │
│   │ Provider (Сервер)    │         │ (Клиент)              │    │
│   │                      │         │                       │    │
│   │ HTTP: 8081           │         │ HTTP: 8082            │    │
│   │ gRPC: 9090           │◄────────│ gRPC Client           │    │
│   │                      │   9090  │                       │    │
│   └──────────────────────┘         └──────────────────────┘    │
│           │                                  │                  │
│           │ gRPC                             │ Console Output   │
│           ▼                                  ▼                  │
│   ┌─────────────────────────────────────────────────────┐       │
│   │            Консоль (логи сервера)                   │       │
│   └─────────────────────────────────────────────────────┘       │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Структура проекта

```
Sem2-SBT/
├── README.md                           # Документация проекта
├── currency-rate-provider/             # gRPC сервер
│   ├── pom.xml                         # Maven конфигурация
│   └── src/main/
│       ├── java/com/example/currencyprovider/
│       │   ├── CurrencyRateProviderApplication.java  # Главный класс
│       │   ├── config/
│       │   │   └── GrpcServerConfig.java              # Конфигурация gRPC сервера
│       │   ├── grpc/
│       │   │   └── CurrencyRateGrpcService.java      # gRPC сервис
│       │   └── service/
│       │       └── CurrencyRateService.java          # Бизнес-логика
│       ├── proto/
│       │   └── currency_rate.proto                    # Protobuf определения
│       └── resources/
│           └── application.properties                 # Настройки приложения
│
└── rate-printer/                       # gRPC клиент
    ├── pom.xml                         # Maven конфигурация
    └── src/main/
        ├── java/com/example/rateprinter/
        │   ├── RatePrinterApplication.java            # Главный класс
        │   └── service/
        │       └── RatePrinterService.java            # gRPC клиент + вывод
        ├── proto/
        │   └── currency_rate.proto                    # Protobuf определения
        └── resources/
            └── application.properties                 # Настройки приложения
```

## Требования

- **Java 17** или выше
- **Maven 3.6** или выше
- Доступ к портам 8081, 8082 и 9090 на localhost

## Конфигурация

### Currency Rate Provider

| Параметр | Значение | Описание |
|----------|----------|----------|
| `server.port` | 8081 | HTTP порт Spring Boot |
| `grpc.server.port` | 9090 | Порт gRPC сервера |

### Rate Printer

| Параметр | Значение | Описание |
|----------|----------|----------|
| `server.port` | 8082 | HTTP порт Spring Boot |
| `grpc.client.currency-provider.host` | localhost | Адрес gRPC сервера |
| `grpc.client.currency-provider.port` | 9090 | Порт gRPC сервера |
| `rate.printer.interval-ms` | 5000 | Интервал запросов (мс) |

## Сборка

### Currency Rate Provider

```bash
cd currency-rate-provider
mvn clean compile
```

### Rate Printer

```bash
cd rate-printer
mvn clean compile
```

## Запуск

**Важно:** Сервер должен быть запущен **перед** клиентом.

1. Запустите Currency Rate Provider:
```bash
cd currency-rate-provider
mvn spring-boot:run
```

2. В отдельном терминале запустите Rate Printer:
```bash
cd rate-printer
mvn spring-boot:run
```

## Проверка работоспособности

После успешного запуска обоих сервисов:

### В консоли Currency Rate Provider вы увидите:
```
2024-01-15T10:30:00.123Z  INFO 12345 --- [           main] c.e.c.CurrencyRateProviderApplication   : Started CurrencyRateProviderApplication in 2.345s
2024-01-15T10:30:00.456Z  INFO 12345 --- [grpc-server] o.grpc.server                         : Server started on port 9090
```

### В консоли Rate Printer вы увидите:
```
2024-01-15T10:30:01.789Z  INFO 12345 --- [           main] c.e.r.RatePrinterApplication         : Started RatePrinterApplication in 1.234s
2024-01-15T10:30:01.890Z  INFO 12345 --- [   scheduler-1] c.e.r.service.RatePrinterService     : Connected to gRPC server at localhost:9090
```

## Логирование

### Пример вывода Rate Printer

Каждые 5 секунд в консоли клиента будет появляться строка с текущим курсом:

```
[RatePrinter] USD/RUB: 92.50 (timestamp: 2024-01-15 10:30:00)
[RatePrinter] USD/RUB: 92.55 (timestamp: 2024-01-15 10:30:05)
[RatePrinter] USD/RUB: 92.48 (timestamp: 2024-01-15 10:30:10)
[RatePrinter] USD/RUB: 92.60 (timestamp: 2024-01-15 10:30:15)
```

Формат вывода:
- `[RatePrinter]` — префикс источника
- `USD/RUB` — валютная пара
- `92.50` — текущий курс
- `timestamp` — время получения курса

## Остановка сервисов

Для остановки сервисов нажмите `Ctrl+C` в терминале, где они запущены.

Рекомендуемый порядок остановки:
1. Сначала остановите Rate Printer (клиент)
2. Затем остановите Currency Rate Provider (сервер)
