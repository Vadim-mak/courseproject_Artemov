package ru.ncfu.touragency.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Укажите имя")
        String fullName,

        @NotBlank @Email(message = "Некорректный email")
        String email,

        @NotBlank @Size(min = 6, message = "Пароль должен быть не короче 6 символов")
        String password,

        String phone
) {}
