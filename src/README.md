# Currency Rate Services (ZooKeeper Discovery + Round Robin)

Проект содержит два Spring Boot микросервиса с gRPC взаимодействием:

- `currency-rate-provider` — gRPC сервер, который отдает курс USD/RUB.
- `rate-printer` — gRPC клиент, который находит provider через ZooKeeper и делает запросы с базовым round-robin выбором инстанса.

## Что реализовано

- Автоматическая регистрация `currency-rate-provider` в ZooKeeper.
- Автоматическое обнаружение инстансов `currency-rate-provider` в `rate-printer`.
- Балансировка вызовов в `rate-printer` через round-robin селектор.
- Unit-тесты для business-логики, gRPC слоя, discovery и round-robin.
- JaCoCo check с порогом покрытия `>= 80%` по строкам в каждом модуле.

## Стек

- Java 17
- Spring Boot 3.2.0
- Spring Cloud 2023.0.0 (ZooKeeper Discovery)
- gRPC + Protobuf
- Maven

## Структура

```text
src/
├── currency-rate-provider/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/example/currencyprovider/
│       ├── proto/currency_rate.proto
│       └── resources/application.properties
├── rate-printer/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/example/rateprinter/
│       ├── proto/currency_rate.proto
│       └── resources/application.properties
├── docker-compose.zookeeper.yml
└── README.md
```

## Конфигурация

### currency-rate-provider

- `spring.application.name=currency-rate-provider`
- `spring.cloud.zookeeper.connect-string=localhost:2181`
- `spring.cloud.zookeeper.discovery.register=true`
- `spring.cloud.zookeeper.discovery.instance-port=${grpc.server.port}`
- `grpc.server.port=9090`

### rate-printer

- `spring.application.name=rate-printer`
- `spring.cloud.zookeeper.connect-string=localhost:2181`
- `rate.provider.service-name=currency-rate-provider`
- `rate.printer.interval-ms=5000`
- `rate.provider.rpc-timeout-ms=1500`

## Запуск ZooKeeper

```bash
cd src
docker compose -f docker-compose.zookeeper.yml up -d
```

Проверка контейнера:

```bash
docker ps | grep sbt-zookeeper
```

## Сборка и тесты

Если локальный `mvn` отсутствует, используйте Maven через Docker.

### currency-rate-provider

```bash
docker run --rm -v "$PWD/src/currency-rate-provider":/workspace -w /workspace \
  maven:3.9.9-eclipse-temurin-17 mvn clean verify
```

### rate-printer

```bash
docker run --rm -v "$PWD/src/rate-printer":/workspace -w /workspace \
  maven:3.9.9-eclipse-temurin-17 mvn clean verify
```

## Запуск сервисов

### Вариант с локальным Maven

1. Запустите provider:

```bash
cd src/currency-rate-provider
mvn spring-boot:run
```

2. В отдельном терминале запустите rate-printer:

```bash
cd src/rate-printer
mvn spring-boot:run
```

### Вариант через Maven Docker

1. Provider:

```bash
docker run --rm --network host -v "$PWD/src/currency-rate-provider":/workspace -w /workspace \
  maven:3.9.9-eclipse-temurin-17 mvn spring-boot:run
```

2. Rate-printer:

```bash
docker run --rm --network host -v "$PWD/src/rate-printer":/workspace -w /workspace \
  maven:3.9.9-eclipse-temurin-17 mvn spring-boot:run
```

## Smoke-проверка

1. Поднять ZooKeeper.
2. Запустить `currency-rate-provider`.
3. Запустить `rate-printer`.
4. Проверить логи `rate-printer`: должны регулярно появляться строки с текущим курсом и адресом provider.

Пример:

```text
[RatePrinter] USD/RUB: 92.73 (timestamp: 2026-02-15 19:20:10, provider: 127.0.0.1:9090)
```

## Остановка

```bash
# остановка сервисов Ctrl+C
cd src
docker compose -f docker-compose.zookeeper.yml down
```
