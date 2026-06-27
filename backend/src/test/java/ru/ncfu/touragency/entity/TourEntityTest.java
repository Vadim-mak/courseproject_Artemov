package ru.ncfu.touragency.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Tour entity — бизнес-методы")
class TourEntityTest {

    private Tour tour;

    @BeforeEach
    void setUp() {
        tour = new Tour();
        tour.setTitle("Тест-тур");
        tour.setPrice(new BigDecimal("10000"));
        tour.setAvailablePlaces(10);
        tour.setDurationDays(7);
        tour.setStartDate(LocalDate.now().plusDays(10));
        tour.setEndDate(LocalDate.now().plusDays(17));
    }

    // ─── hasAvailablePlaces ───────────────────────────────────────────

    @Nested
    @DisplayName("hasAvailablePlaces")
    class HasAvailablePlaces {

        @Test
        @DisplayName("возвращает true, когда мест достаточно и тур активен")
        void returnsTrue_whenEnoughPlacesAndActive() {
            assertThat(tour.hasAvailablePlaces(5)).isTrue();
        }

        @Test
        @DisplayName("возвращает true при запросе ровно всех мест")
        void returnsTrue_whenRequestingExactly_allPlaces() {
            assertThat(tour.hasAvailablePlaces(10)).isTrue();
        }

        @Test
        @DisplayName("возвращает false, когда мест меньше запрошенного")
        void returnsFalse_whenNotEnoughPlaces() {
            assertThat(tour.hasAvailablePlaces(11)).isFalse();
        }

        @Test
        @DisplayName("возвращает false, когда тур деактивирован")
        void returnsFalse_whenTourInactive() {
            tour.deactivate();
            assertThat(tour.hasAvailablePlaces(1)).isFalse();
        }

        @Test
        @DisplayName("возвращает false при нулевом количестве мест")
        void returnsFalse_whenZeroPlacesAvailable() {
            tour.setAvailablePlaces(0);
            assertThat(tour.hasAvailablePlaces(1)).isFalse();
        }
    }

    // ─── reservePlaces ────────────────────────────────────────────────

    @Nested
    @DisplayName("reservePlaces")
    class ReservePlaces {

        @Test
        @DisplayName("уменьшает количество мест на запрошенное число")
        void decreasesAvailablePlaces() {
            tour.reservePlaces(3);
            assertThat(tour.getAvailablePlaces()).isEqualTo(7);
        }

        @Test
        @DisplayName("бронирует все оставшиеся места")
        void reservesAllPlaces() {
            tour.reservePlaces(10);
            assertThat(tour.getAvailablePlaces()).isEqualTo(0);
        }

        @Test
        @DisplayName("бросает IllegalStateException, когда мест не хватает")
        void throwsWhenNotEnoughPlaces() {
            assertThatThrownBy(() -> tour.reservePlaces(11))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Недостаточно свободных мест");
        }

        @Test
        @DisplayName("бросает IllegalStateException для деактивированного тура")
        void throwsWhenTourInactive() {
            tour.deactivate();
            assertThatThrownBy(() -> tour.reservePlaces(1))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    // ─── releasePlaces ────────────────────────────────────────────────

    @Nested
    @DisplayName("releasePlaces")
    class ReleasePlaces {

        @Test
        @DisplayName("возвращает места при отмене бронирования")
        void increasesAvailablePlaces() {
            tour.setAvailablePlaces(7);
            tour.releasePlaces(3);
            assertThat(tour.getAvailablePlaces()).isEqualTo(10);
        }

        @Test
        @DisplayName("корректно работает при возврате всех мест")
        void releasesAllPlaces() {
            tour.setAvailablePlaces(0);
            tour.releasePlaces(10);
            assertThat(tour.getAvailablePlaces()).isEqualTo(10);
        }
    }

    // ─── calculateTotalPrice ─────────────────────────────────────────

    @Nested
    @DisplayName("calculateTotalPrice")
    class CalculateTotalPrice {

        @Test
        @DisplayName("считает цену для одного человека")
        void singlePerson() {
            assertThat(tour.calculateTotalPrice(1))
                    .isEqualByComparingTo(new BigDecimal("10000"));
        }

        @Test
        @DisplayName("считает цену для группы туристов")
        void groupOfPeople() {
            assertThat(tour.calculateTotalPrice(3))
                    .isEqualByComparingTo(new BigDecimal("30000"));
        }

        @Test
        @DisplayName("цена для нуля туристов равна нулю")
        void zeroPeople() {
            assertThat(tour.calculateTotalPrice(0))
                    .isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    // ─── deactivate ───────────────────────────────────────────────────

    @Test
    @DisplayName("deactivate() устанавливает active = false")
    void deactivate_setsActiveFalse() {
        assertThat(tour.isActive()).isTrue();
        tour.deactivate();
        assertThat(tour.isActive()).isFalse();
    }
}
