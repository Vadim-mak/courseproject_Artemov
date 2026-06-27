package ru.ncfu.touragency.mediator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.ncfu.touragency.dto.request.LoginRequest;
import ru.ncfu.touragency.dto.request.RegisterRequest;
import ru.ncfu.touragency.dto.response.AuthResponse;
import ru.ncfu.touragency.entity.Role;
import ru.ncfu.touragency.entity.User;
import ru.ncfu.touragency.entity.enums.RoleName;
import ru.ncfu.touragency.exception.BusinessRuleException;
import ru.ncfu.touragency.foundation.RoleRepository;
import ru.ncfu.touragency.foundation.UserRepository;
import ru.ncfu.touragency.security.JwtUtils;
import ru.ncfu.touragency.security.UserDetailsImpl;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl — unit-тесты с Mockito")
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock AuthenticationManager authenticationManager;
    @Mock JwtUtils jwtUtils;

    @InjectMocks AuthServiceImpl authService;

    private Role userRole;
    private User savedUser;

    @BeforeEach
    void setUp() {
        userRole = new Role(RoleName.ROLE_USER);

        savedUser = new User("Тест Пользователь", "test@test.com", "encoded_pass", "+79000000000");
        setId(savedUser, 1L);
        savedUser.addRole(userRole);
    }

    // ─── register ────────────────────────────────────────────────────

    @Nested
    @DisplayName("register()")
    class Register {

        @Test
        @DisplayName("успешная регистрация возвращает токен и данные пользователя")
        void registersSuccessfully() {
            RegisterRequest request = new RegisterRequest(
                    "Тест Пользователь", "test@test.com", "password123", "+79000000000");

            when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("encoded_pass");
            when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(userRole));
            when(userRepository.save(any(User.class))).thenReturn(savedUser);
            when(jwtUtils.generateToken("test@test.com", 1L)).thenReturn("jwt.token.here");

            AuthResponse response = authService.register(request);

            assertThat(response.token()).isEqualTo("jwt.token.here");
            assertThat(response.userId()).isEqualTo(1L);
            assertThat(response.email()).isEqualTo("test@test.com");
            assertThat(response.fullName()).isEqualTo("Тест Пользователь");
            assertThat(response.roles()).containsExactly(RoleName.ROLE_USER.name());
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("создаёт роль ROLE_USER если её нет в БД")
        void createsRoleIfNotExists() {
            RegisterRequest request = new RegisterRequest(
                    "Новый", "new@test.com", "pass123", null);

            when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
            when(passwordEncoder.encode("pass123")).thenReturn("enc");
            when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.empty());
            when(roleRepository.save(any(Role.class))).thenReturn(userRole);
            when(userRepository.save(any(User.class))).thenReturn(savedUser);
            when(jwtUtils.generateToken(anyString(), any())).thenReturn("token");

            authService.register(request);

            verify(roleRepository).save(any(Role.class));
        }

        @Test
        @DisplayName("бросает BusinessRuleException при дублировании email")
        void throwsWhenEmailAlreadyExists() {
            RegisterRequest request = new RegisterRequest(
                    "Дубль", "test@test.com", "pass123", null);
            when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("уже зарегистрирован");

            verify(userRepository, never()).save(any());
        }
    }

    // ─── login ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("login()")
    class Login {

        @Test
        @DisplayName("успешный логин возвращает JWT токен")
        void loginSuccessfully() {
            LoginRequest request = new LoginRequest("test@test.com", "password123");
            UserDetailsImpl principal = new UserDetailsImpl(savedUser);

            var authentication = new UsernamePasswordAuthenticationToken(
                    principal, null, principal.getAuthorities());
            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(jwtUtils.generateToken("test@test.com", 1L)).thenReturn("jwt.token.login");

            AuthResponse response = authService.login(request);

            assertThat(response.token()).isEqualTo("jwt.token.login");
            assertThat(response.email()).isEqualTo("test@test.com");
        }

        @Test
        @DisplayName("BadCredentialsException при неверных данных")
        void throwsBadCredentials() {
            LoginRequest request = new LoginRequest("test@test.com", "wrongpass");
            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BadCredentialsException.class);
        }
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
