package ru.ncfu.touragency.dto.response;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Integer rating,
        String comment,
        LocalDateTime createdAt,
        String userFullName,
        Long tourId
) {}
