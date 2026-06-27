package ru.ncfu.touragency.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        boolean active,
        LocalDateTime registeredAt,
        List<String> roles
) {}
