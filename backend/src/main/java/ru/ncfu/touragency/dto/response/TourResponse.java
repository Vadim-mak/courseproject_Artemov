package ru.ncfu.touragency.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TourResponse(
        Long id,
        String title,
        String description,
        BigDecimal price,
        Integer durationDays,
        LocalDate startDate,
        LocalDate endDate,
        Integer availablePlaces,
        String imageUrl,
        boolean active,
        CountryResponse country,
        Double averageRating,
        Integer reviewsCount
) {}
