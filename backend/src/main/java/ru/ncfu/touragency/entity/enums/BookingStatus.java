package ru.ncfu.touragency.entity.enums;

/**
 * Статус бронирования тура.
 */
public enum BookingStatus {
    PENDING,     // создано, ожидает подтверждения менеджером
    CONFIRMED,   // подтверждено
    CANCELLED    // отменено пользователем или администратором
}
