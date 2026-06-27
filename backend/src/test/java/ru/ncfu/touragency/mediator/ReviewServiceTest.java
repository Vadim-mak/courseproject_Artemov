package ru.ncfu.touragency.mediator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ncfu.touragency.dto.request.ReviewRequest;
import ru.ncfu.touragency.dto.response.ReviewResponse;
import ru.ncfu.touragency.entity.Review;
import ru.ncfu.touragency.entity.Tour;
import ru.ncfu.touragency.entity.User;
import ru.ncfu.touragency.exception.AccessDeniedAppException;
import ru.ncfu.touragency.exception.BusinessRuleException;
import ru.ncfu.touragency.exception.ResourceNotFoundException;
import ru.ncfu.touragency.foundation.ReviewRepository;
import ru.ncfu.touragency.foundation.TourRepository;
import ru.ncfu.touragency.foundation.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewServiceImpl — unit-тесты с Mockito")
class ReviewServiceTest {

    @Mock ReviewRepository reviewRepository;
    @Mock TourRepository tourRepository;
    @Mock UserRepository userRepository;

    @InjectMocks ReviewServiceImpl reviewService;

    private Tour tour;
    private User user;

    @BeforeEach
    void setUp() {
        tour = new Tour();
        setId(tour, 10L);
        tour.setTitle("Хургада: дайвинг");
        tour.setPrice(new BigDecimal("52000"));
        tour.setAvailablePlaces(15);
        tour.setDurationDays(10);
        tour.setStartDate(LocalDate.now().plusDays(5));
        tour.setEndDate(LocalDate.now().plusDays(15));

        user = new User("Анна Смирнова", "anna@test.com", "pass", null);
        setId(user, 7L);
    }

    // ─── getByTour ────────────────────────────────────────────────────

    @Test
    @DisplayName("getByTour() возвращает список отзывов тура")
    void getByTour_returnsList() {
        Review review = new Review(user, tour, 4, "Понравилось");
        setId(review, 1L);
        when(reviewRepository.findByTourIdOrderByCreatedAtDesc(10L)).thenReturn(List.of(review));

        List<ReviewResponse> result = reviewService.getByTour(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).rating()).isEqualTo(4);
        assertThat(result.get(0).userFullName()).isEqualTo("Анна Смирнова");
    }

    @Test
    @DisplayName("getByTour() возвращает пустой список, если отзывов нет")
    void getByTour_returnsEmptyList() {
        when(reviewRepository.findByTourIdOrderByCreatedAtDesc(10L)).thenReturn(List.of());
        assertThat(reviewService.getByTour(10L)).isEmpty();
    }

    // ─── create ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("создаёт отзыв, если пользователь ещё не оставлял отзыв")
        void createsReview_whenNoExistingReview() {
            ReviewRequest request = new ReviewRequest(5, "Отлично!");
            Review saved = new Review(user, tour, 5, "Отлично!");
            setId(saved, 1L);

            when(reviewRepository.existsByUserIdAndTourId(7L, 10L)).thenReturn(false);
            when(tourRepository.findById(10L)).thenReturn(Optional.of(tour));
            when(userRepository.findById(7L)).thenReturn(Optional.of(user));
            when(reviewRepository.save(any(Review.class))).thenReturn(saved);

            ReviewResponse response = reviewService.create(10L, 7L, request);

            assertThat(response.rating()).isEqualTo(5);
            assertThat(response.comment()).isEqualTo("Отлично!");
            assertThat(response.tourId()).isEqualTo(10L);
            verify(reviewRepository).save(any(Review.class));
        }

        @Test
        @DisplayName("бросает BusinessRuleException, если отзыв уже оставлен")
        void throwsWhenAlreadyReviewed() {
            when(reviewRepository.existsByUserIdAndTourId(7L, 10L)).thenReturn(true);

            assertThatThrownBy(() -> reviewService.create(10L, 7L, new ReviewRequest(3, "Неплохо")))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("уже оставляли отзыв");

            verify(reviewRepository, never()).save(any());
        }

        @Test
        @DisplayName("бросает ResourceNotFoundException, если тур не найден")
        void throwsWhenTourNotFound() {
            when(reviewRepository.existsByUserIdAndTourId(7L, 99L)).thenReturn(false);
            when(tourRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reviewService.create(99L, 7L, new ReviewRequest(4, "ok")))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("бросает ResourceNotFoundException, если пользователь не найден")
        void throwsWhenUserNotFound() {
            when(reviewRepository.existsByUserIdAndTourId(99L, 10L)).thenReturn(false);
            when(tourRepository.findById(10L)).thenReturn(Optional.of(tour));
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reviewService.create(10L, 99L, new ReviewRequest(4, "ok")))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ─── delete ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("автор может удалить свой отзыв")
        void authorCanDeleteOwnReview() {
            Review review = new Review(user, tour, 5, "Отлично");
            setId(review, 1L);
            when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

            reviewService.delete(1L, 7L, false);

            verify(reviewRepository).delete(review);
        }

        @Test
        @DisplayName("администратор может удалить любой отзыв")
        void adminCanDeleteAnyReview() {
            Review review = new Review(user, tour, 5, "Отлично");
            setId(review, 1L);
            when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

            reviewService.delete(1L, 999L, true);

            verify(reviewRepository).delete(review);
        }

        @Test
        @DisplayName("посторонний получает AccessDeniedAppException")
        void strangerGetsForbidden() {
            Review review = new Review(user, tour, 5, "Отлично");
            setId(review, 1L);
            when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

            assertThatThrownBy(() -> reviewService.delete(1L, 999L, false))
                    .isInstanceOf(AccessDeniedAppException.class);

            verify(reviewRepository, never()).delete(any());
        }

        @Test
        @DisplayName("бросает ResourceNotFoundException, если отзыв не найден")
        void throwsWhenReviewNotFound() {
            when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reviewService.delete(99L, 7L, false))
                    .isInstanceOf(ResourceNotFoundException.class);
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
