package ru.ncfu.touragency.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.ncfu.touragency.entity.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Booking entity — бизнес-методы")
class BookingEntityTest {

    private Tour tour;
    private User user;
    private Booking booking;

    @BeforeEach
    void setUp() {
        tour = new Tour();
        tour.setTitle("Тест-тур");
        tour.setPrice(new BigDecimal("15000"));
        tour.setAvailablePlaces(10);
        tour.setDurationDays(7);
        tour.setStartDate(LocalDate.now().plusDays(10));
        tour.setEndDate(LocalDate.now().plusDays(17));

        user = new User("Иван Иванов", "ivan@test.com", "encoded_pass", "+79001112233");
        // устанавливаем ID через рефлексию (в тестах без БД)
        setId(user, 42L);

        booking = new Booking(user, tour, 2);
    }

    // ─── конструктор ─────────────────────────────────────────────────

    @Nested
    @DisplayName("Конструктор Booking")
    class Constructor {

        @Test
        @DisplayName("устанавливает статус PENDING при создании")
        void statusIsPendingOnCreation() {
            assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);
        }

        @Test
        @DisplayName("корректно вычисляет totalPrice")
        void calculatesCorrectTotalPrice() {
            assertThat(booking.getTotalPrice())
                    .isEqualByComparingTo(new BigDecimal("30000")); // 15000 * 2
        }

        @Test
        @DisplayName("генерирует уникальный confirmationCode")
        void generatesConfirmationCode() {
            Booking another = new Booking(user, tour, 1);
            assertThat(booking.getConfirmationCode()).isNotNull().isNotBlank();
            assertThat(booking.getConfirmationCode())
                    .isNotEqualTo(another.getConfirmationCode());
        }

        @Test
        @DisplayName("сохраняет количество туристов")
        void storesNumberOfPeople() {
            assertThat(booking.getNumberOfPeople()).isEqualTo(2);
        }
    }

    // ─── confirm ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("confirm()")
    class Confirm {

        @Test
        @DisplayName("меняет статус PENDING → CONFIRMED")
        void confirmsFromPending() {
            booking.confirm();
            assertThat(booking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        }

        @Test
        @DisplayName("бросает IllegalStateException при попытке подтвердить CONFIRMED")
        void throwsWhenAlreadyConfirmed() {
            booking.confirm();
            assertThatThrownBy(booking::confirm)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PENDING");
        }

        @Test
        @DisplayName("бросает IllegalStateException при попытке подтвердить CANCELLED")
        void throwsWhenCancelled() {
            booking.cancel();
            assertThatThrownBy(booking::confirm)
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    // ─── cancel ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("cancel()")
    class Cancel {

        @Test
        @DisplayName("меняет статус PENDING → CANCELLED")
        void cancelsFromPending() {
            booking.cancel();
            assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        }

        @Test
        @DisplayName("возвращает места туру при отмене")
        void releasesPlacesToTour() {
            int placesBefore = tour.getAvailablePlaces();
            booking.cancel();
            assertThat(tour.getAvailablePlaces()).isEqualTo(placesBefore + 2);
        }

        @Test
        @DisplayName("можно отменить CONFIRMED бронирование")
        void cancelsFromConfirmed() {
            booking.confirm();
            booking.cancel();
            assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        }

        @Test
        @DisplayName("бросает IllegalStateException при повторной отмене")
        void throwsWhenAlreadyCancelled() {
            booking.cancel();
            assertThatThrownBy(booking::cancel)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("уже отменено");
        }
    }

    // ─── belongsTo ────────────────────────────────────────────────────

    @Nested
    @DisplayName("belongsTo()")
    class BelongsTo {

        @Test
        @DisplayName("возвращает true для владельца бронирования")
        void trueForOwner() {
            assertThat(booking.belongsTo(42L)).isTrue();
        }

        @Test
        @DisplayName("возвращает false для другого пользователя")
        void falseForOtherUser() {
            assertThat(booking.belongsTo(99L)).isFalse();
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
