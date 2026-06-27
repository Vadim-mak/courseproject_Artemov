package ru.ncfu.touragency.dto.response;

import java.util.List;

public record AuthResponse(
        String token,
        Long userId,
        String fullName,
        String email,
        List<String> roles
) {}
