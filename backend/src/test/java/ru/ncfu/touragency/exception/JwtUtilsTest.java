package ru.ncfu.touragency.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ncfu.touragency.security.JwtUtils;

import static org.assertj.core.api.Assertions.*;

@DisplayName("JwtUtils — генерация и валидация JWT")
class JwtUtilsTest {

    private JwtUtils jwtUtils;
    // Base64-кодированный ключ ≥ 256 бит
    private static final String SECRET = "VGhpcytpcy1hLXZlcnktbG9uZy1zZWNyZXQta2V5LWZvci10b3VyLWFnZW5jeS1KV1Q=";

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(SECRET, 3_600_000L); // 1 час
    }

    @Test
    @DisplayName("generateToken() создаёт непустой токен")
    void generateToken_notBlank() {
        String token = jwtUtils.generateToken("user@test.com", 1L);
        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("extractEmail() возвращает email из токена")
    void extractEmail_returnsSubject() {
        String token = jwtUtils.generateToken("user@test.com", 1L);
        assertThat(jwtUtils.extractEmail(token)).isEqualTo("user@test.com");
    }

    @Test
    @DisplayName("extractUserId() возвращает userId из токена")
    void extractUserId_returnsId() {
        String token = jwtUtils.generateToken("user@test.com", 42L);
        assertThat(jwtUtils.extractUserId(token)).isEqualTo(42L);
    }

    @Test
    @DisplayName("validateToken() возвращает true для валидного токена")
    void validateToken_trueForValid() {
        String token = jwtUtils.generateToken("user@test.com", 1L);
        assertThat(jwtUtils.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("validateToken() возвращает false для мусорной строки")
    void validateToken_falseForJunk() {
        assertThat(jwtUtils.validateToken("not.a.jwt.token")).isFalse();
    }

    @Test
    @DisplayName("validateToken() возвращает false для пустой строки")
    void validateToken_falseForEmpty() {
        assertThat(jwtUtils.validateToken("")).isFalse();
    }

    @Test
    @DisplayName("validateToken() возвращает false для истёкшего токена")
    void validateToken_falseForExpiredToken() {
        JwtUtils shortLived = new JwtUtils(SECRET, -1000L); // уже истёк
        String token = shortLived.generateToken("user@test.com", 1L);
        assertThat(jwtUtils.validateToken(token)).isFalse();
    }

    @Test
    @DisplayName("два токена для разных пользователей содержат разные email")
    void differentTokensForDifferentUsers() {
        String token1 = jwtUtils.generateToken("alice@test.com", 1L);
        String token2 = jwtUtils.generateToken("bob@test.com", 2L);

        assertThat(jwtUtils.extractEmail(token1)).isEqualTo("alice@test.com");
        assertThat(jwtUtils.extractEmail(token2)).isEqualTo("bob@test.com");
    }
}
