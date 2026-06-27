package ru.ncfu.touragency.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank @Email(message = "Некорректный email")
        String email,

        @NotBlank
        String password
) {}
