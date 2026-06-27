package ru.ncfu.touragency.exception;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("GlobalExceptionHandler — unit-тесты всех обработчиков")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("ResourceNotFoundException → 404 Not Found")
    void handleNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Ресурс id=42 не найден");
        ResponseEntity<ApiError> response = handler.handleNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().error()).isEqualTo("Not Found");
        assertThat(response.getBody().message()).isEqualTo("Ресурс id=42 не найден");
    }

    @Test
    @DisplayName("BusinessRuleException → 409 Conflict")
    void handleBusinessRule() {
        BusinessRuleException ex = new BusinessRuleException("Email уже зарегистрирован");
        ResponseEntity<ApiError> response = handler.handleBusinessRule(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().status()).isEqualTo(409);
        assertThat(response.getBody().error()).isEqualTo("Conflict");
        assertThat(response.getBody().message()).contains("Email");
    }

    @Test
    @DisplayName("AccessDeniedAppException → 403 Forbidden")
    void handleAccessDeniedAppException() {
        AccessDeniedAppException ex = new AccessDeniedAppException("Нет доступа");
        ResponseEntity<ApiError> response = handler.handleForbidden(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().status()).isEqualTo(403);
        assertThat(response.getBody().error()).isEqualTo("Forbidden");
    }

    @Test
    @DisplayName("Spring AccessDeniedException → 403 Forbidden")
    void handleSpringAccessDeniedException() {
        AccessDeniedException ex = new AccessDeniedException("Access is denied");
        ResponseEntity<ApiError> response = handler.handleForbidden(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().status()).isEqualTo(403);
    }

    @Test
    @DisplayName("BadCredentialsException → 401 Unauthorized")
    void handleBadCredentials() {
        BadCredentialsException ex = new BadCredentialsException("Bad credentials");
        ResponseEntity<ApiError> response = handler.handleBadCredentials(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().status()).isEqualTo(401);
        assertThat(response.getBody().error()).isEqualTo("Unauthorized");
        assertThat(response.getBody().message()).isEqualTo("Неверный email или пароль");
    }

    @Test
    @DisplayName("MethodArgumentNotValidException → 400 с деталями полей")
    void handleMethodArgumentNotValid() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("obj", "email", "Некорректный email");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ApiError> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().error()).isEqualTo("Validation Failed");
        assertThat(response.getBody().details()).containsExactly("email: Некорректный email");
    }

    @Test
    @DisplayName("ConstraintViolationException → 400 Bad Request")
    void handleConstraintViolation() {
        ConstraintViolationException ex = new ConstraintViolationException("constraint violation", Set.of());
        ResponseEntity<ApiError> response = handler.handleConstraintViolation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().status()).isEqualTo(400);
    }

    @Test
    @DisplayName("IllegalArgumentException → 400 Bad Request")
    void handleIllegalArgument() {
        IllegalArgumentException ex = new IllegalArgumentException("Недопустимое значение");
        ResponseEntity<ApiError> response = handler.handleIllegalArgument(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("Недопустимое значение");
    }

    @Test
    @DisplayName("IllegalStateException → 409 Conflict")
    void handleIllegalState() {
        IllegalStateException ex = new IllegalStateException("Недостаточно мест");
        ResponseEntity<ApiError> response = handler.handleIllegalState(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().status()).isEqualTo(409);
    }

    @Test
    @DisplayName("Exception (generic) → 500 Internal Server Error")
    void handleGenericException() {
        Exception ex = new RuntimeException("Что-то пошло не так");
        ResponseEntity<ApiError> response = handler.handleGeneric(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().error()).isEqualTo("Internal Server Error");
    }
}
