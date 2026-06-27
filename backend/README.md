# Tour Agency Backend (Spring Boot)

Серверная часть информационной системы туристического агентства.
Реализует слои **Control / Mediator / Entity / Foundation** архитектуры
**PCMEF** (Presentation вынесен в мобильный клиент `/mobile`).

## Стек

- Java 17, Spring Boot 3.3
- Spring Web, Spring Data JPA, Spring Security
- PostgreSQL
- JWT (jjwt)
- springdoc-openapi (Swagger UI)
- JUnit 5 + Mockito + AssertJ

## Структура (PCMEF)

```
src/main/java/ru/ncfu/touragency/
├── control/      # REST-контроллеры (Control)
├── mediator/      # бизнес-логика, интерфейсы IxxxService + impl (Mediator)
├── entity/        # JPA-сущности с бизнес-методами (Entity)
├── foundation/     # Spring Data репозитории (Foundation)
├── security/       # JWT, UserDetails, SecurityConfig
├── dto/            # request/response контракты API
├── exception/      # обработка ошибок
└── config/         # OpenAPI, сидер демо-данных
```

Зависимости слоёв направлены строго сверху вниз: `control → mediator → entity ← foundation`.
Control никогда не обращается к Foundation напрямую — только через интерфейсы Mediator
(`IUserService`, `ITourService`, `IBookingService`, `IReviewService`, `ICountryService`, `IAuthService`).

## Запуск локально

1. Поднять PostgreSQL и создать БД (см. `database/ddl.sql`, либо позволить Hibernate
   создать схему автоматически — `ddl-auto: update` уже включён в `application.yml`):

```bash
createdb touragency
psql -d touragency -c "CREATE USER touragency_user WITH PASSWORD 'touragency_pass';"
psql -d touragency -c "GRANT ALL PRIVILEGES ON DATABASE touragency TO touragency_user;"
```

2. Запустить приложение:

```bash
cd backend
mvn spring-boot:run
```

При первом запуске `DataSeeder` создаст роли, администратора и демо-каталог туров:

```
email: admin@touragency.local
password: Admin123!
```

3. Swagger UI: http://localhost:8080/swagger-ui.html
   OpenAPI JSON: http://localhost:8080/api-docs

## Тесты

```bash
mvn test
```

Покрыты бизнес-методы Entity (`Tour.reservePlaces`, `Booking.cancel`) и
Mediator-сервисы (`TourServiceImpl`, `BookingServiceImpl`) с использованием Mockito.

## Основные эндпоинты

| Метод  | Путь                              | Доступ          |
|--------|-----------------------------------|-----------------|
| POST   | /api/auth/register                | публичный       |
| POST   | /api/auth/login                   | публичный       |
| GET    | /api/tours                        | публичный       |
| GET    | /api/tours/search                 | публичный       |
| GET    | /api/tours/{id}                   | публичный       |
| POST   | /api/tours                        | ROLE_ADMIN      |
| PUT    | /api/tours/{id}                   | ROLE_ADMIN      |
| DELETE | /api/tours/{id}                   | ROLE_ADMIN      |
| GET    | /api/countries                    | публичный       |
| POST   | /api/bookings/tour/{tourId}       | аутентифиц.     |
| GET    | /api/bookings/my                  | аутентифиц.     |
| DELETE | /api/bookings/{id}                | владелец/админ  |
| PUT    | /api/bookings/{id}/confirm        | ROLE_ADMIN      |
| GET    | /api/bookings                     | ROLE_ADMIN      |
| GET    | /api/reviews/tour/{tourId}        | публичный       |
| POST   | /api/tours/{tourId}/reviews       | аутентифиц.     |
| GET    | /api/users/me                     | аутентифиц.     |
| PUT    | /api/users/me                     | аутентифиц.     |
| GET    | /api/users                        | ROLE_ADMIN      |

Полный список — в Swagger UI.

## Docker (опционально, бонус)

См. `docker-compose.yml` в корне репозитория.
