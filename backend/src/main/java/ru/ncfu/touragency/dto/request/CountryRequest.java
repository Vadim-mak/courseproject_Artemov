package ru.ncfu.touragency.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CountryRequest(
        @NotBlank(message = "Укажите название страны")
        String name,

        String description,

        String imageUrl
) {}
