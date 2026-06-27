# Информационная система туристического агентства

**Курсовой проект по дисциплине «Программная инженерия»**
Северо-Кавказский федеральный университет, 2026 г.

- **Направление:** 09.03.04 «Программная инженерия»
- **Траектория:** В — Мобильная разработка
- **Архитектура:** PCMEF (Presentation-Control-Mediator-Entity-Foundation)

## Состав проекта

```
TourAgency-MobileApp/
├── backend/      # Spring Boot — Control / Mediator / Entity / Foundation
├── mobile/       # React Native (Expo) — Presentation
├── database/     # DDL-скрипты PostgreSQL
├── docs/         # Документация по этапам курсового проекта
└── docker-compose.yml
```

## Быстрый старт

```bash
# 1. Запуск backend + PostgreSQL через Docker:
docker-compose up -d

# 2. Запуск мобильного клиента:
cd mobile && npm install && npm start
```

Swagger UI: http://localhost:8080/swagger-ui.html

Учётная запись администратора (демо): `admin@touragency.local` / `Admin123!`

## Архитектура PCMEF

```
┌─────────────────────────────────────────────┐
│   КЛИЕНТ (React Native)                      │
│   ┌─────────────────┐                        │
│   │  Presentation   │  screens/, components/ │
│   └────────┬────────┘                        │
│            │ REST / JSON (HTTP)              │
└────────────┼────────────────────────────────┘
             │
┌────────────┼────────────────────────────────┐
│   СЕРВЕР (Spring Boot)                       │
│   ┌────────▼────────┐                        │
│   │    Control      │  @RestController        │
│   └────────┬────────┘                        │
│            │ IxxxService                     │
│   ┌────────▼────────┐                        │
│   │    Mediator     │  @Service + транзакции │
│   └────────┬────────┘                        │
│            │ бизнес-методы Entity            │
│   ┌────────▼────────┐                        │
│   │     Entity      │  JPA + бизнес-логика   │
│   └────────┬────────┘                        │
│            │ IxxxRepository                  │
│   ┌────────▼────────┐                        │
│   │   Foundation    │  Spring Data JPA       │
│   └────────┬────────┘                        │
│            ▼                                 │
│      PostgreSQL 16                           │
└─────────────────────────────────────────────┘
```

## Статистика разработки

### Метрики Git

- Всего коммитов: *(заполнить перед защитой)*
- Период: 01.03.2026 – 30.05.2026
- Средняя частота: *(заполнить)*

### График активности

![Активность коммитов](docs/images/git-commit-activity.png)

### Тепловая карта

![Распределение по времени](docs/images/git-punch-card.png)

## Документация

- [docs/01-business-model](docs/01-business-model/README.md) — Бизнес-анализ, IDEF0
- [docs/02-requirements](docs/02-requirements/README.md) — Use Case, Domain Model
- [docs/03-architecture](docs/03-architecture/README.md) — Диаграмма пакетов PCMEF
- [docs/04-database](docs/04-database/README.md) — ER-диаграмма, DDL
- [docs/05-design](docs/05-design/README.md) — Диаграммы последовательности
- [docs/06-implementation](docs/06-implementation/README.md) — Структура кода, тесты
- [docs/07-ui](docs/07-ui/README.md) — Скриншоты интерфейсов
- [docs/08-final](docs/08-final/) — ТЗ, руководства, пояснительная записка
