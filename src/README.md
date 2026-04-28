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
- **12-Factor App**: разделение сборки/релиза/выполнения (Docker), graceful shutdown, dev/prod профили, stdout логирование, shared proto модуль.

## Стек

- Java 17
- Spring Boot 3.2.0
- Spring Cloud 2023.0.0 (ZooKeeper Discovery)
- gRPC + Protobuf
- Maven
- Docker

## Структура

```text
src/
├── currency-rate-proto/          # Общие protobuf-определения
│   ├── pom.xml
│   └── src/main/proto/currency_rate.proto
├── currency-rate-provider/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/example/currencyprovider/
│       └── resources/
│           ├── application.properties
│           ├── application-dev.properties
│           └── application-prod.properties
├── rate-printer/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/example/rateprinter/
│       └── resources/
│           ├── application.properties
│           ├── application-dev.properties
│           └── application-prod.properties
├── docker-compose.yml             # Полный стек (ZooKeeper + сервисы + мониторинг)
├── docker-compose.zookeeper.yml
├── docker-compose.monitoring.yml
├── docker-compose.pact-broker.yml
├── pom.xml                        # Корневой агрегирующий POM
└── README.md
```

## Профили Spring Boot

- **dev** (по умолчанию при локальном запуске) — ZooKeeper на `localhost:2181`, debug-логи.
- **prod** (по умолчанию в Docker) — ZooKeeper через `ZOOKEEPER_CONNECT_STRING` env var (`zookeeper:2181` в compose), info-логи.

Активировать профиль:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
# или
java -jar app.jar --spring.profiles.active=prod
```

## Graceful shutdown

При получении `SIGTERM` приложение:
1. Останавливает приём новых HTTP/gRPC запросов.
2. Дожидается завершения текущих запросов (до 30 секунд).
3. Корректно завершает процесс.

Настройки:
- `server.shutdown=graceful`
- `spring.lifecycle.timeout-per-shutdown-phase=30s`
- `grpc.server.shutdown-grace-period=30` (provider)

## Запуск через Docker Compose (production-like)

```bash
cd src
docker compose up -d --build
```

Это поднимет:
- ZooKeeper
- `currency-rate-provider` (2 реплики по умолчанию)
- `rate-printer`
- Prometheus
- Grafana

Остановка:
```bash
docker compose down
```

## Запуск ZooKeeper (только инфраструктура)

```bash
cd src
docker compose -f docker-compose.zookeeper.yml up -d
```

## Сборка и тесты

### Все модули через корневой POM

```bash
cd src
mvn clean verify
```

### Через Docker Maven (если локальный Maven отсутствует)

```bash
cd src

# Сначала proto модуль
docker run --rm -v "$PWD":/workspace -w /workspace \
  maven:3.9.9-eclipse-temurin-17 mvn clean install -pl currency-rate-proto

# Затем сервисы
docker run --rm -v "$PWD/currency-rate-provider":/workspace -w /workspace \
  maven:3.9.9-eclipse-temurin-17 mvn clean verify

docker run --rm -v "$PWD/rate-printer":/workspace -w /workspace \
  maven:3.9.9-eclipse-temurin-17 mvn clean verify
```

## Запуск сервисов (локальная разработка)

1. Запустите provider:

```bash
cd src/currency-rate-provider
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

2. В отдельном терминале запустите rate-printer:

```bash
cd src/rate-printer
mvn spring-boot:run -Dspring-boot.run.profiles=dev
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
docker compose down
docker compose -f docker-compose.zookeeper.yml down
```
