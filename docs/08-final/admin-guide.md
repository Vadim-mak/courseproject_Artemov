# Руководство администратора
## Информационная система туристического агентства

---

## 1. Системные требования

### Сервер

| Компонент | Минимум | Рекомендуется |
|---|---|---|
| ОС | Ubuntu 20.04 / Windows 10 | Ubuntu 22.04 LTS |
| JDK | Java 17 | Java 21 |
| RAM | 512 МБ | 1 ГБ |
| Место на диске | 500 МБ | 2 ГБ |
| PostgreSQL | 14 | 16 |

### Мобильное устройство разработчика

- Node.js 18+
- npm 9+
- Expo CLI (`npm install -g expo-cli`)
- Android Studio (для Android-эмулятора) или Xcode (для iOS-симулятора, только macOS)

---

## 2. Установка и первый запуск

### 2.1 Вариант А: через Docker (рекомендуется)

```bash
# 1. Клонировать репозиторий
git clone https://github.com/student-name/tour-agency-mobile-app.git
cd tour-agency-mobile-app

# 2. Запустить PostgreSQL + Backend одной командой
docker-compose up -d

# Проверить статус:
docker-compose ps

# Логи backend:
docker-compose logs -f backend
```

Swagger UI будет доступен по адресу: http://localhost:8080/swagger-ui.html

### 2.2 Вариант Б: ручная установка

**PostgreSQL:**

```sql
-- Создать базу данных и пользователя:
CREATE DATABASE touragency;
CREATE USER touragency_user WITH PASSWORD 'touragency_pass';
GRANT ALL PRIVILEGES ON DATABASE touragency TO touragency_user;
GRANT ALL ON SCHEMA public TO touragency_user;
```

**Backend:**

```bash
cd backend
mvn clean package -DskipTests
java -jar target/tour-agency-backend.jar
```

Или в режиме разработки:

```bash
mvn spring-boot:run
```

**Инициализация БД:** При первом запуске `DataSeeder` автоматически создаёт:
- Роли: ROLE_USER, ROLE_ADMIN
- Администратора: `admin@touragency.local` / `Admin123!`
- 3 страны и 3 демо-тура

---

## 3. Настройка

### 3.1 Параметры backend (`backend/src/main/resources/application.yml`)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/touragency    # адрес БД
    username: touragency_user
    password: touragency_pass

app:
  jwt:
    secret: "ChangeThisSuperSecretKeyForTourAgencyCourseProject2026!!"
    access-token-expiration-ms: 3600000    # 1 час
```

> ⚠️ В production обязательно замените `jwt.secret` на случайную строку длиной 64+ символа.

### 3.2 Адрес сервера в мобильном клиенте (`mobile/src/api/client.ts`)

```typescript
const resolveBaseUrl = () => {
  if (Platform.OS === "android") {
    return "http://10.0.2.2:8080/api";    // Android-эмулятор
  }
  return "http://localhost:8080/api";     // iOS-симулятор / web
};
```

Для физического устройства замените на реальный IP сервера:

```typescript
return "http://192.168.1.100:8080/api";
```

---

## 4. Управление через мобильное приложение

Войдите с учётной записью администратора. В нижней панели появится вкладка **«Админ»**.

### 4.1 Управление бронированиями

- Просмотр всех бронирований с фильтром по статусу
- Кнопка **«Подтвердить»** — переводит бронирование в статус CONFIRMED
- Кнопка **«Отменить»** — переводит в статус CANCELLED, места возвращаются в тур

### 4.2 Управление турами (через Swagger UI или curl)

```bash
# Получить JWT-токен:
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@touragency.local","password":"Admin123!"}'

# Создать тур (подставить token из предыдущего ответа):
curl -X POST http://localhost:8080/api/tours \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Рим: вечный город",
    "description": "5 дней в Риме",
    "price": 75000,
    "durationDays": 5,
    "startDate": "2026-07-01",
    "endDate": "2026-07-06",
    "availablePlaces": 20,
    "countryId": 1
  }'
```

---

## 5. Мониторинг и логи

```bash
# Логи в Docker:
docker-compose logs -f backend

# Логи при ручном запуске — в консоль Spring Boot.
# Уровень логирования настраивается в application.yml:
logging:
  level:
    ru.ncfu.touragency: DEBUG
```

---

## 6. Обновление системы

```bash
# 1. Остановить контейнеры:
docker-compose down

# 2. Обновить код:
git pull

# 3. Пересобрать и запустить:
docker-compose up -d --build
```

Схема БД обновляется автоматически через Hibernate (`ddl-auto: update`).
Для production рекомендуется переключить на `validate` и применять миграции вручную.

---

## 7. Резервное копирование БД

```bash
# Создать дамп:
pg_dump -U touragency_user touragency > backup_$(date +%Y%m%d).sql

# Восстановить:
psql -U touragency_user touragency < backup_20260530.sql
```
