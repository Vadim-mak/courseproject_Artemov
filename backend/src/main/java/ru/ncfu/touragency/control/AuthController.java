package ru.ncfu.touragency.control;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ncfu.touragency.dto.request.LoginRequest;
import ru.ncfu.touragency.dto.request.RegisterRequest;
import ru.ncfu.touragency.dto.response.AuthResponse;
import ru.ncfu.touragency.mediator.IAuthService;

/**
 * Control-слой (PCMEF). Обрабатывает HTTP-запросы и делегирует
 * бизнес-логику в Mediator (IAuthService). Сам не содержит правил.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Регистрация и аутентификация пользователей")
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Регистрация нового туриста")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Вход по email и паролю, возвращает JWT")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
