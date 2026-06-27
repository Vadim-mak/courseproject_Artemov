package ru.ncfu.touragency.control;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.ncfu.touragency.dto.request.UpdateProfileRequest;
import ru.ncfu.touragency.dto.response.UserResponse;
import ru.ncfu.touragency.mediator.IUserService;
import ru.ncfu.touragency.security.SecurityUtils;

import java.util.List;

/**
 * Control-слой (PCMEF). Профиль текущего пользователя и
 * административное управление пользователями.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Профиль и управление пользователями")
public class UserController {

    private final IUserService userService;

    @GetMapping("/me")
    @Operation(summary = "Профиль текущего пользователя")
    public UserResponse me() {
        return userService.getById(SecurityUtils.currentUserId());
    }

    @PutMapping("/me")
    @Operation(summary = "Обновить профиль текущего пользователя")
    public UserResponse updateMe(@Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateProfile(SecurityUtils.currentUserId(), request);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Список всех пользователей (только администратор)")
    public List<UserResponse> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Пользователь по id (только администратор)")
    public UserResponse getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Деактивировать пользователя (только администратор)")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        userService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
