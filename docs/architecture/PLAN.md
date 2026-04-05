# Plan: Архитектура MVP цифрового банка в РФ

## Context
Репозиторий пуст (только initial commit). Нужно создать полноценный аналитический отчет — исследование архитектурных паттернов банковских систем + проектирование MVP-архитектуры цифрового банка/необанка в России. Документ должен быть пригоден для лабораторной работы, архитектурного отчета и презентации.

Ветка: `claude/banking-architecture-design-YwmrD`

## File Structure

```
docs/
  architecture/
    banking-mvp-architecture.md    # Основной документ (~3000-4000 строк)
README.md                          # Краткое описание проекта с навигацией
```

**Решение**: один большой Markdown-файл с inline Mermaid-диаграммами. Это удобнее для чтения, поиска, экспорта в PDF/презентацию и навигации по оглавлению. Разбиение на файлы избыточно для документа-исследования.

## Implementation Steps

### Step 1: Create directory structure
- `mkdir -p docs/architecture`

### Step 2: Write main document — `docs/architecture/banking-mvp-architecture.md`
Документ пишется секциями в порядке из ТЗ (17 разделов). Из-за размера (~3500+ строк), документ будет создаваться блоками через Write/Edit.

#### Sections & Key Content:

**1. Executive Summary** (~30 строк)
- Выбранный подход: гибридная архитектура (локальная АБС + микросервисный слой)
- MVP-продуктовый набор
- Обоснование типичности для рынка РФ

**2. Исследование паттернов** (~400 строк)
- Таблица сравнения: монолит, модульный монолит, SOA, микросервисы, event-driven, hybrid, API-first, DDD, CQRS/ES
- Для каждого: суть, плюсы, минусы, применимость в банках, пригодность для MVP РФ, риски

**3. Контекст российского рынка** (~200 строк)
- Продукты на старте / отложенные
- Обязательные интеграции (СБП, НСПК, КYC/AML, ЕСИА)
- Регуляторные ограничения (152-ФЗ, 115-ФЗ, ЦБ)
- Отличия от глобального необанка

**4. Архитектурные принципы MVP** (~100 строк)
- 12-15 принципов с пояснениями

**5. Целевая архитектура MVP** (~300 строк)
- Описание слоев: channels, BFF/API GW, IAM, domain services, orchestration, event bus, core/АБС, DB, analytics, integration, external
- Что в MVP vs Phase 2
- Синхронные vs асинхронные потоки

**6. Bounded Contexts** (~400 строк)
- 11 контекстов: Customer, Onboarding/KYC, Accounts, Cards, Payments, SBP, Notifications, Fees/Tariffs, Fraud/Risk, Ledger/Posting, Reporting/Compliance
- Для каждого: ответственность, сущности, API/события, зависимости, выделение в сервис

**7. Выбор Core — ADR** (~200 строк)
- ADR формат: Context, Decision, Alternatives, Consequences
- Сравнение: локальная АБС, собственный ledger, BaaS
- Trade-offs таблица

**8. Компонентные диаграммы Mermaid** (~200 строк)
- C4 Context diagram
- C4 Container diagram
- Component diagram для платежного сценария

**9. Бизнес-сценарии** (~500 строк)
- 7 сценариев: регистрация, идентификация, открытие счета, выпуск карты, перевод СБП, оплата, начисление процентов
- Для каждого: инициатор, участники, данные, синхронность, события, ledger, проверки

**10. Data Architecture** (~250 строк)
- Core vs domain data
- Transactional vs eventual consistency
- Outbox, Saga, idempotency patterns
- Audit log, event versioning
- Operational vs analytical контуры

**11. Матрица интеграций** (~200 строк)
- Таблица: система, назначение, обязательность, тип, данные, риски
- СБП, НСПК/Мир, KYC/AML, SMS/email/push, ЕСИА/СМЭВ, антифрод, регуляторный контур, DWH

**12. NFR** (~200 строк)
- Реалистичные для MVP: availability 99.9%, latency targets, RPO/RTO, scalability, security, observability, backup, DR, release strategy

**13. Безопасность и комплаенс** (~200 строк)
- IAM, MFA, secrets, encryption, audit, PCI DSS, ПДн, RBAC, secure SDLC

**14. Инфраструктура** (~200 строк)
- Private cloud / colocation в РФ
- Kubernetes
- CI/CD, мониторинг, логирование
- Сегментация контуров

**15. Дорожная карта** (~150 строк)
- MVP / Phase 2 / Phase 3
- Таблица: фаза, сервисы, продукты

**16. Риски и компромиссы** (~100 строк)
- Таблица: решение, выгода, риск, митигация

**17. Итоговая рекомендация** (~150 строк)
- Конкретное проектное решение
- Список ключевых архитектурных решений
- Открытые вопросы
- Следующие артефакты

### Step 3: Create README.md
- Описание проекта, навигация по документу

### Step 4: Commit and push
- Коммит с описательным сообщением
- Push на ветку `claude/banking-architecture-design-YwmrD`

## Mermaid Diagrams (minimum 6):
1. C4 Context — банк и внешние системы
2. C4 Container — внутренние контейнеры/сервисы
3. Component diagram — платежный flow
4. Sequence diagram — перевод по СБП
5. Sequence diagram — регистрация/onboarding
6. Bounded contexts map

## Tables (minimum 8):
1. Сравнение архитектурных паттернов
2. Продукты MVP vs Later
3. Архитектурные принципы
4. Bounded contexts summary
5. Core choice comparison (ADR)
6. Integration matrix
7. NFR targets
8. Risks & trade-offs

## Verification
- Markdown renders correctly (headings, tables, Mermaid blocks)
- All 17 sections present with substantive content
- Document is self-contained and presentation-ready
- Git push successful to target branch
