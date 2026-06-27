-- =====================================================================
-- Информационная система туристического агентства
-- DDL-скрипт PostgreSQL (этап 3 курсового проекта "Проектирование БД")
-- Соответствует JPA-сущностям backend/.../entity/*.java
-- Нормализация: 3НФ
-- =====================================================================

CREATE DATABASE touragency;
-- \c touragency

CREATE TABLE roles (
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(30) NOT NULL UNIQUE CHECK (name IN ('ROLE_USER', 'ROLE_ADMIN'))
);

CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    full_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(150) NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    phone           VARCHAR(30),
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    registered_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE countries (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL UNIQUE,
    description     VARCHAR(2000),
    image_url       VARCHAR(500)
);

CREATE TABLE tours (
    id                  BIGSERIAL PRIMARY KEY,
    title               VARCHAR(150) NOT NULL,
    description         VARCHAR(3000),
    price               NUMERIC(10,2) NOT NULL CHECK (price > 0),
    duration_days       INTEGER NOT NULL CHECK (duration_days > 0),
    start_date          DATE NOT NULL,
    end_date            DATE NOT NULL CHECK (end_date >= start_date),
    available_places    INTEGER NOT NULL CHECK (available_places >= 0),
    image_url           VARCHAR(500),
    active              BOOLEAN NOT NULL DEFAULT TRUE,
    country_id          BIGINT NOT NULL REFERENCES countries(id)
);

CREATE TABLE bookings (
    id                  BIGSERIAL PRIMARY KEY,
    confirmation_code   VARCHAR(36) NOT NULL UNIQUE,
    booking_date        TIMESTAMP NOT NULL DEFAULT NOW(),
    number_of_people    INTEGER NOT NULL CHECK (number_of_people > 0),
    total_price         NUMERIC(10,2) NOT NULL CHECK (total_price >= 0),
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                            CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED')),
    user_id             BIGINT NOT NULL REFERENCES users(id),
    tour_id             BIGINT NOT NULL REFERENCES tours(id)
);

CREATE TABLE reviews (
    id              BIGSERIAL PRIMARY KEY,
    rating          INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment         VARCHAR(1000),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    user_id         BIGINT NOT NULL REFERENCES users(id),
    tour_id         BIGINT NOT NULL REFERENCES tours(id),
    UNIQUE (user_id, tour_id)   -- один отзыв от пользователя на тур
);

-- =====================================================================
-- Индексы для часто запрашиваемых полей
-- =====================================================================
CREATE INDEX idx_tours_country       ON tours(country_id);
CREATE INDEX idx_tours_active        ON tours(active);
CREATE INDEX idx_tours_price         ON tours(price);
CREATE INDEX idx_bookings_user       ON bookings(user_id);
CREATE INDEX idx_bookings_tour       ON bookings(tour_id);
CREATE INDEX idx_bookings_status     ON bookings(status);
CREATE INDEX idx_reviews_tour        ON reviews(tour_id);

-- =====================================================================
-- Учётная запись приложения (использовать вместо суперпользователя)
-- =====================================================================
-- CREATE USER touragency_user WITH PASSWORD 'touragency_pass';
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO touragency_user;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO touragency_user;
