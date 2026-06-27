package ru.ncfu.touragency.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.ncfu.touragency.dto.response.PageResponse;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ApiError и PageResponse — unit-тесты")
class ApiErrorAndPageResponseTest {

    // ─── ApiError ─────────────────────────────────────────────────────

    @Test
    @DisplayName("ApiError(3 аргумента) — details пустой, timestamp заполнен")
    void apiError_threeArgs() {
        ApiError error = new ApiError(404, "Not Found", "Не найдено");

        assertThat(error.status()).isEqualTo(404);
        assertThat(error.error()).isEqualTo("Not Found");
        assertThat(error.message()).isEqualTo("Не найдено");
        assertThat(error.details()).isEmpty();
        assertThat(error.timestamp()).isNotNull();
        assertThat(error.timestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("ApiError(4 аргумента) — details заполнен")
    void apiError_fourArgs() {
        List<String> details = List.of("email: некорректный", "password: слишком короткий");
        ApiError error = new ApiError(400, "Validation Failed", "Ошибка валидации", details);

        assertThat(error.status()).isEqualTo(400);
        assertThat(error.details()).containsExactly("email: некорректный", "password: слишком короткий");
    }

    @Test
    @DisplayName("ApiError: разные статусы корректно хранятся")
    void apiError_variousStatuses() {
        assertThat(new ApiError(200, "OK", "ok").status()).isEqualTo(200);
        assertThat(new ApiError(401, "Unauthorized", "unauth").status()).isEqualTo(401);
        assertThat(new ApiError(500, "ISE", "err").status()).isEqualTo(500);
    }

    // ─── PageResponse ─────────────────────────────────────────────────

    @Test
    @DisplayName("PageResponse.from() корректно конвертирует Spring Page")
    void pageResponse_fromSpringPage() {
        List<String> content = List.of("item1", "item2", "item3");
        Page<String> page = new PageImpl<>(content, PageRequest.of(0, 10), 25L);

        PageResponse<String> response = PageResponse.from(page);

        assertThat(response.content()).containsExactly("item1", "item2", "item3");
        assertThat(response.page()).isEqualTo(0);
        assertThat(response.size()).isEqualTo(10);
        assertThat(response.totalElements()).isEqualTo(25L);
        assertThat(response.totalPages()).isEqualTo(3); // ceil(25/10)
    }

    @Test
    @DisplayName("PageResponse.from() для второй страницы")
    void pageResponse_secondPage() {
        List<String> content = List.of("item11", "item12");
        Page<String> page = new PageImpl<>(content, PageRequest.of(1, 10), 12L);

        PageResponse<String> response = PageResponse.from(page);

        assertThat(response.page()).isEqualTo(1);
        assertThat(response.totalElements()).isEqualTo(12L);
        assertThat(response.totalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("PageResponse.from() для пустой страницы")
    void pageResponse_emptyPage() {
        Page<String> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0L);

        PageResponse<String> response = PageResponse.from(page);

        assertThat(response.content()).isEmpty();
        assertThat(response.totalElements()).isEqualTo(0L);
        assertThat(response.totalPages()).isEqualTo(0);
    }
}
