package ru.ncfu.touragency.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Review entity — конструктор и валидация рейтинга")
class ReviewEntityTest {

    private User user;
    private Tour tour;

    @BeforeEach
    void setUp() {
        user = new User("Мария Иванова", "maria@test.com", "pass", null);

        tour = new Tour();
        tour.setTitle("Тур в Египет");
        tour.setPrice(new BigDecimal("52000"));
        tour.setAvailablePlaces(15);
        tour.setDurationDays(10);
        tour.setStartDate(LocalDate.now().plusDays(5));
        tour.setEndDate(LocalDate.now().plusDays(15));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    @DisplayName("корректный рейтинг 1-5 создаёт Review без исключений")
    void validRatingsCreateReview(int rating) {
        assertThatNoException().isThrownBy(
                () -> new Review(user, tour, rating, "Отличный тур!"));
    }

    @Test
    @DisplayName("Review сохраняет все переданные поля")
    void reviewStoresAllFields() {
        Review review = new Review(user, tour, 5, "Всё понравилось");
        assertThat(review.getRating()).isEqualTo(5);
        assertThat(review.getComment()).isEqualTo("Всё понравилось");
        assertThat(review.getUser()).isSameAs(user);
        assertThat(review.getTour()).isSameAs(tour);
        assertThat(review.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Review с null-комментарием создаётся корректно")
    void allowsNullComment() {
        assertThatNoException().isThrownBy(
                () -> new Review(user, tour, 3, null));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 6, 100})
    @DisplayName("недопустимый рейтинг бросает IllegalArgumentException")
    void invalidRatingThrows(int badRating) {
        assertThatThrownBy(() -> new Review(user, tour, badRating, "comment"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Оценка должна быть от 1 до 5");
    }

    @Test
    @DisplayName("null-рейтинг бросает IllegalArgumentException")
    void nullRatingThrows() {
        assertThatThrownBy(() -> new Review(user, tour, null, "comment"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
