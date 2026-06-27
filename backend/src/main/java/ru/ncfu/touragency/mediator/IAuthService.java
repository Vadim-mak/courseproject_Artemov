package ru.ncfu.touragency.mediator;

import ru.ncfu.touragency.dto.request.LoginRequest;
import ru.ncfu.touragency.dto.request.RegisterRequest;
import ru.ncfu.touragency.dto.response.AuthResponse;

/**
 * Mediator-слой (PCMEF) — контракт бизнес-логики аутентификации.
 * Control-слой (AuthController) зависит только от этого интерфейса.
 */
public interface IAuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
