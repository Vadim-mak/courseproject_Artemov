package ru.ncfu.touragency.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BookingRequest(
        @NotNull @Min(value = 1, message = "Количество туристов должно быть не менее 1")
        Integer numberOfPeople
) {}
