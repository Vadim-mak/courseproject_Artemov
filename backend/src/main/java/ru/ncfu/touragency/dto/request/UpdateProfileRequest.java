package ru.ncfu.touragency.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequest(
        @NotBlank(message = "Укажите имя")
        String fullName,

        String phone
) {}
