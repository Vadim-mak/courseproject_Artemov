# Этап 5-6: Реализация ядра и рефакторинг

## Структура backend-проекта

```
backend/src/main/java/ru/ncfu/touragency/
│
├── TourAgencyApplication.java        # точка входа
│
├── control/                          # ── Control-слой (PCMEF) ──
│   ├── AuthController.java           # POST /auth/register, /auth/login
│   ├── TourController.java           # CRUD /tours, поиск /tours/search
│   ├── BookingController.java        # бронирование, отмена, подтверждение
│   ├── ReviewController.java         # отзывы на туры
│   ├── CountryController.java        # справочник направлений
│   └── UserController.java           # профиль, управление пользователями
│
├── mediator/                         # ── Mediator-слой (PCMEF) ──
│   ├── IAuthService.java             # контракт аутентификации
│   ├── AuthServiceImpl.java          # регистрация + вход + JWT
│   ├── ITourService.java
│   ├── TourServiceImpl.java          # каталог, поиск, CRUD, средний рейтинг
│   ├── IBookingService.java
│   ├── BookingServiceImpl.java       # бронирование, отмена (транзакции)
│   ├── IReviewService.java
│   ├── ReviewServiceImpl.java
│   ├── ICountryService.java
│   ├── CountryServiceImpl.java
│   ├── IUserService.java
│   └── UserServiceImpl.java
│
├── entity/                           # ── Entity-слой (PCMEF) ──
│   ├── User.java                     # бизнес-методы: addRole, deactivate, isAdmin
│   ├── Tour.java                     # reservePlaces, releasePlaces, calculateTotalPrice
│   ├── Booking.java                  # confirm, cancel, belongsTo
│   ├── Review.java                   # validateRating в конструкторе
│   ├── Country.java
│   ├── Role.java
│   └── enums/
│       ├── BookingStatus.java
│       └── RoleName.java
│
├── foundation/                       # ── Foundation-слой (PCMEF) ──
│   ├── UserRepository.java
│   ├── TourRepository.java           # search() с JPQL-фильтрацией
│   ├── BookingRepository.java
│   ├── ReviewRepository.java
│   ├── CountryRepository.java
│   └── RoleRepository.java
│
├── security/                         # ── Инфраструктура безопасности ──
│   ├── JwtUtils.java                 # генерация и валидация JWT
│   ├── JwtAuthFilter.java            # OncePerRequestFilter
│   ├── SecurityConfig.java           # правила доступа ROLE_USER/ADMIN, CORS
│   ├── UserDetailsImpl.java          # адаптер User → Spring Security
│   ├── UserDetailsServiceImpl.java
│   └── SecurityUtils.java            # currentUserId(), isAdmin()
│
├── dto/
│   ├── request/                      # RegisterRequest, LoginRequest, TourRequest,
│   │                                 # BookingRequest, ReviewRequest, ...
│   └── response/                     # AuthResponse, TourResponse, BookingResponse,
│                                     # PageResponse<T>, ...
│
├── exception/
│   ├── GlobalExceptionHandler.java   # @RestControllerAdvice → ApiError
│   ├── ResourceNotFoundException.java
│   ├── BusinessRuleException.java
│   ├── AccessDeniedAppException.java
│   └── ApiError.java                 # стандартный формат ошибки API
│
└── config/
    ├── OpenApiConfig.java            # Swagger UI с JWT-схемой
    └── DataSeeder.java               # демо-данные при первом запуске
```

## Ключевые паттерны реализации

### Data Mapper (обязательный паттерн, §2.7)

Преобразование JPA-сущностей в DTO и обратно выполняется в Mediator-слое в методах `toResponse()`.
Это отделяет доменные объекты от транспортного контракта API и реализует паттерн **Data Mapper**:

```java
// TourServiceImpl.java — Data Mapper: Tour → TourResponse
private TourResponse toResponse(Tour t) {
    List<Review> reviews = reviewRepository.findByTourIdOrderByCreatedAtDesc(t.getId());
    Double avg = reviews.isEmpty() ? null :
            reviews.stream().mapToInt(Review::getRating).average().orElse(0);
    CountryResponse countryResponse = new CountryResponse(...);
    return new TourResponse(t.getId(), t.getTitle(), ...);
}
```

### Identity Map (через Hibernate Session)

Spring Data JPA + Hibernate автоматически обеспечивает **Identity Map** на уровне сессии:
каждая JPA-сущность (`Tour`, `User`, `Booking`) существует в единственном экземпляре
в пределах одной транзакции, что гарантирует отсутствие дублирования объектов.

### Не-анемичная модель (Entity с бизнес-методами)

```java
// Tour.java — бизнес-логика в Entity, не в Mediator
public void reservePlaces(int count) {
    if (!hasAvailablePlaces(count)) {
        throw new IllegalStateException("Недостаточно свободных мест в туре \"" + title + "\"");
    }
    this.availablePlaces -= count;
}

// Booking.java
public void cancel() {
    if (this.status == BookingStatus.CANCELLED) {
        throw new IllegalStateException("Бронирование уже отменено");
    }
    this.tour.releasePlaces(this.numberOfPeople);
    this.status = BookingStatus.CANCELLED;
}
```

## Модульное тестирование

| Класс теста | Слой | Охват |
|---|---|---|
| `TourServiceImplTest` | Mediator | create, getEntityOrThrow |
| `BookingServiceImplTest` | Mediator | createBooking, cancelBooking (доступ) |
| `TourAndBookingEntityTest` | Entity | reservePlaces, cancel, calculateTotalPrice, belongsTo |

Запуск:
```bash
cd backend && mvn test
```

Для отчёта покрытия (JaCoCo):
```bash
mvn test jacoco:report
# Отчёт: target/site/jacoco/index.html
```

## REST API — итоговый список (15+ эндпоинтов)

| # | Метод | Путь | Доступ | Описание |
|---|---|---|---|---|
| 1 | POST | /api/auth/register | публичный | Регистрация |
| 2 | POST | /api/auth/login | публичный | Вход, возврат JWT |
| 3 | GET | /api/tours | публичный | Список туров (пагинация) |
| 4 | GET | /api/tours/search | публичный | Поиск с фильтрацией |
| 5 | GET | /api/tours/{id} | публичный | Детали тура |
| 6 | POST | /api/tours | ADMIN | Создать тур |
| 7 | PUT | /api/tours/{id} | ADMIN | Обновить тур |
| 8 | DELETE | /api/tours/{id} | ADMIN | Деактивировать тур |
| 9 | GET | /api/countries | публичный | Список направлений |
| 10 | POST | /api/countries | ADMIN | Создать направление |
| 11 | PUT | /api/countries/{id} | ADMIN | Обновить направление |
| 12 | DELETE | /api/countries/{id} | ADMIN | Удалить направление |
| 13 | POST | /api/bookings/tour/{id} | USER | Забронировать тур |
| 14 | GET | /api/bookings/my | USER | Мои бронирования |
| 15 | GET | /api/bookings/{id} | USER/ADMIN | Бронирование по id |
| 16 | GET | /api/bookings/confirmation/{code} | USER | По коду подтверждения |
| 17 | DELETE | /api/bookings/{id} | USER/ADMIN | Отменить бронирование |
| 18 | PUT | /api/bookings/{id}/confirm | ADMIN | Подтвердить бронирование |
| 19 | GET | /api/bookings | ADMIN | Все бронирования |
| 20 | GET | /api/reviews/tour/{id} | публичный | Отзывы о туре |
| 21 | POST | /api/tours/{id}/reviews | USER | Оставить отзыв |
| 22 | DELETE | /api/reviews/{id} | USER/ADMIN | Удалить отзыв |
| 23 | GET | /api/users/me | USER | Мой профиль |
| 24 | PUT | /api/users/me | USER | Обновить профиль |
| 25 | GET | /api/users | ADMIN | Все пользователи |
| 26 | GET | /api/users/{id} | ADMIN | Пользователь по id |
| 27 | DELETE | /api/users/{id} | ADMIN | Деактивировать пользователя |
