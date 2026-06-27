# Проектирование базы данных

## ER-диаграмма

```plantuml
@startuml
skinparam linetype ortho

entity "roles" as R {
  *id : BIGSERIAL <<PK>>
  --
  *name : VARCHAR(30) UNIQUE
}

entity "users" as U {
  *id : BIGSERIAL <<PK>>
  --
  *full_name : VARCHAR(100)
  *email : VARCHAR(150) UNIQUE
  *password : VARCHAR(255)
  phone : VARCHAR(30)
  *active : BOOLEAN = true
  *registered_at : TIMESTAMP
}

entity "user_roles" as UR {
  *user_id <<FK>>
  *role_id <<FK>>
}

entity "countries" as CO {
  *id : BIGSERIAL <<PK>>
  --
  *name : VARCHAR(100) UNIQUE
  description : VARCHAR(2000)
  image_url : VARCHAR(500)
}

entity "tours" as T {
  *id : BIGSERIAL <<PK>>
  --
  *title : VARCHAR(150)
  description : VARCHAR(3000)
  *price : NUMERIC(10,2) > 0
  *duration_days : INTEGER > 0
  *start_date : DATE
  *end_date : DATE
  *available_places : INTEGER >= 0
  image_url : VARCHAR(500)
  *active : BOOLEAN = true
  *country_id <<FK>>
}

entity "bookings" as B {
  *id : BIGSERIAL <<PK>>
  --
  *confirmation_code : VARCHAR(36) UNIQUE
  *booking_date : TIMESTAMP
  *number_of_people : INTEGER > 0
  *total_price : NUMERIC(10,2)
  *status : VARCHAR(20)
  *user_id <<FK>>
  *tour_id <<FK>>
}

entity "reviews" as REV {
  *id : BIGSERIAL <<PK>>
  --
  *rating : INTEGER [1..5]
  comment : VARCHAR(1000)
  *created_at : TIMESTAMP
  *user_id <<FK>>
  *tour_id <<FK>>
  UNIQUE(user_id, tour_id)
}

U ||--o{ UR : "имеет"
R ||--o{ UR : "назначена"
CO ||--o{ T : "содержит"
U ||--o{ B : "создаёт"
T ||--o{ B : "бронируется"
U ||--o{ REV : "пишет"
T ||--o{ REV : "получает"

@enduml
```

## Описание таблиц

| Таблица | Назначение | NF |
|---|---|---|
| roles | Роли (ROLE_USER, ROLE_ADMIN) | 3НФ |
| users | Пользователи | 3НФ |
| user_roles | Связь пользователей с ролями (M:N) | 3НФ |
| countries | Страны и направления | 3НФ |
| tours | Туры | 3НФ |
| bookings | Бронирования | 3НФ |
| reviews | Отзывы (уникальность: 1 отзыв/пользователь/тур) | 3НФ |

DDL-скрипт: [`../../database/ddl.sql`](../../database/ddl.sql)

## Стратегия ORM (маппинг Entity → таблица)

| JPA-сущность | Таблица | Особенности |
|---|---|---|
| `User` | `users` | `@UniqueConstraint(email)` |
| `Role` | `roles` | `@Enumerated(STRING)` |
| `Country` | `countries` | — |
| `Tour` | `tours` | `@ManyToOne(country)`, soft delete через `active` |
| `Booking` | `bookings` | `@Enumerated(STRING)` для status |
| `Review` | `reviews` | `@Table(uniqueConstraints = {user_id, tour_id})` |
