package ru.ncfu.touragency.mediator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ncfu.touragency.dto.request.UpdateProfileRequest;
import ru.ncfu.touragency.dto.response.UserResponse;
import ru.ncfu.touragency.entity.Role;
import ru.ncfu.touragency.entity.User;
import ru.ncfu.touragency.entity.enums.RoleName;
import ru.ncfu.touragency.exception.ResourceNotFoundException;
import ru.ncfu.touragency.foundation.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl — unit-тесты с Mockito")
class UserServiceTest {

    @Mock UserRepository userRepository;
    @InjectMocks UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Дмитрий Козлов", "dmitry@test.com", "pass", "+79007778899");
        setId(user, 3L);
        user.addRole(new Role(RoleName.ROLE_USER));
    }

    // ─── getById ──────────────────────────────────────────────────────

    @Test
    @DisplayName("getById() возвращает UserResponse по существующему id")
    void getById_returnsUser() {
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));

        UserResponse response = userService.getById(3L);

        assertThat(response.id()).isEqualTo(3L);
        assertThat(response.fullName()).isEqualTo("Дмитрий Козлов");
        assertThat(response.email()).isEqualTo("dmitry@test.com");
        assertThat(response.active()).isTrue();
        assertThat(response.roles()).containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("getById() бросает ResourceNotFoundException при отсутствии")
    void getById_throwsWhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ─── updateProfile ────────────────────────────────────────────────

    @Test
    @DisplayName("updateProfile() обновляет имя и телефон")
    void updateProfile_updatesFields() {
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        UpdateProfileRequest request = new UpdateProfileRequest("Дмитрий Н. Козлов", "+79001234567");

        UserResponse response = userService.updateProfile(3L, request);

        assertThat(response.fullName()).isEqualTo("Дмитрий Н. Козлов");
        assertThat(response.phone()).isEqualTo("+79001234567");
    }

    @Test
    @DisplayName("updateProfile() бросает ResourceNotFoundException, если пользователь не найден")
    void updateProfile_throwsWhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                userService.updateProfile(99L, new UpdateProfileRequest("Имя", null)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── getAll ───────────────────────────────────────────────────────

    @Test
    @DisplayName("getAll() возвращает список пользователей")
    void getAll_returnsList() {
        User second = new User("Ольга Сидорова", "olga@test.com", "pass", null);
        setId(second, 4L);
        when(userRepository.findAll()).thenReturn(List.of(user, second));

        List<UserResponse> result = userService.getAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(UserResponse::email)
                .containsExactlyInAnyOrder("dmitry@test.com", "olga@test.com");
    }

    // ─── deactivate ───────────────────────────────────────────────────

    @Test
    @DisplayName("deactivate() устанавливает active=false")
    void deactivate_setsInactive() {
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));

        userService.deactivate(3L);

        assertThat(user.isActive()).isFalse();
    }

    @Test
    @DisplayName("deactivate() бросает ResourceNotFoundException для несуществующего id")
    void deactivate_throwsWhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deactivate(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── вспомогательный метод ────────────────────────────────────────

    private static void setId(Object obj, Long id) {
        try {
            var field = obj.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(obj, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
