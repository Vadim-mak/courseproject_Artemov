package ru.ncfu.touragency.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BookingResponse(
        Long id,
        String confirmationCode,
        LocalDateTime bookingDate,
        Integer numberOfPeople,
        BigDecimal totalPrice,
        String status,
        TourResponse tour,
        Long userId,
        String userFullName
) {}
