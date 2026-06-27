# Информационная система туристического агентства

## О проекте
**TourAgency-MobileApp** — информационная система туристического агентства, разработанная в рамках курсового проекта по дисциплине «Программная инженерия».

Проект выполнен по направлению **09.03.04 «Программная инженерия»**, траектория **В — Мобильная разработка**.

Система предназначена для автоматизации работы туристического агентства: управление турами, пользователями, бронированиями и административными процессами.

Приложение включает:

- регистрацию и авторизацию пользователей (JWT);
- разделение ролей USER / ADMIN;
- просмотр и управление турами;
- систему бронирования;
- административную панель;
- REST API взаимодействие между клиентом и сервером;
- мобильный клиент на React Native (Expo);
- серверную часть на Spring Boot;
- хранение данных в PostgreSQL.

---

## Используемый стек технологий

### Mobile (Presentation)
- React Native
- Expo
- TypeScript
- Axios
- React Navigation
- AsyncStorage
- Context API

### Backend (PCMEF)
- Java 21
- Spring Boot 3.4.x
- Spring Security
- Spring Data JPA
- JWT Authentication
- PostgreSQL 16
- Swagger / OpenAPI
- Maven
- JUnit 5
- Mockito
- JaCoCo

---

## Ключевые факты о репозитории

| Показатель | Значение |
|------------|----------|
| Архитектура | PCMEF |
| Backend | Spring Boot |
| Mobile | React Native + Expo |
| БД | PostgreSQL |
| API стиль | REST |
| Документация | docs/ (8 разделов) |
| Контейнеризация | Docker Compose |

---

## Структура проекта


TourAgency-MobileApp/
├── backend/ # Spring Boot (Control / Mediator / Entity / Foundation)
├── mobile/ # React Native (Presentation layer)
├── database/ # SQL DDL скрипты PostgreSQL
├── docs/ # Документация проекта
└── docker-compose.yml


---

## Быстрый старт

# Запуск backend + PostgreSQL
mvn spring-boot:run

# Запуск мобильного приложения
cd mobile
npm install
npx expo start    

http://localhost:8080/swagger-ui.html

Архитектура (PCMEF)

Проект реализован по архитектуре PCMEF (Presentation-Control-Mediator-Entity-Foundation):

Слой	Расположение	Ответственность
Presentation	mobile/	UI, экраны, навигация
Control	backend/controller	REST API, входные запросы
Mediator	backend/service	бизнес-логика и транзакции
Entity	backend/entity	доменные модели (JPA)
Foundation	repository, config	доступ к БД и конфигурация
REST API (основные эндпоинты)
Аутентификация
POST /api/auth/register
POST /api/auth/login
Пользователь
GET /api/profile/me
PUT /api/profile/me
Туры и бронирования
GET /api/tours
POST /api/bookings
GET /api/bookings/me
Админ-панель
GET /api/admin/users
Документация

Папка docs/ содержит:

бизнес-анализ (IDEF0)
требования (Use Case, Domain Model)
архитектура PCMEF
ER-диаграмма базы данных
диаграммы последовательностей
описание реализации
UI-скриншоты
итоговая пояснительная записка
Статистика разработки
Backend: Spring Boot 3.4.x
Mobile: React Native + Expo
Архитектура: PCMEF
REST endpoints: реализованы
Документация: 8 разделов
Контейнеризация: Docker Compose
Скриншоты и схемы
Интерфейс мобильного приложения
ER-диаграмма базы данных
PCMEF архитектура
Use Case диаграммы
Диаграммы последовательностей


Итог
Проект представляет собой полнофункциональную клиент-серверную систему туристического агентства с мобильным приложением и REST backend-ом, реализованную с применением современной архитектуры PCMEF и практик программной инженерии.
