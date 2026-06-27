package ru.ncfu.touragency.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TourRequest(
        @NotBlank(message = "Укажите название тура")
        String title,

        @Size(max = 3000)
        String description,

        @NotNull @DecimalMin(value = "0.0", inclusive = false, message = "Цена должна быть больше нуля")
        BigDecimal price,

        @NotNull @Min(value = 1, message = "Длительность минимум 1 день")
        Integer durationDays,

        @NotNull
        LocalDate startDate,

        @NotNull
        LocalDate endDate,

        @NotNull @Min(value = 0, message = "Количество мест не может быть отрицательным")
        Integer availablePlaces,

        String imageUrl,

        @NotNull(message = "Укажите страну/направление")
        Long countryId
) {}
