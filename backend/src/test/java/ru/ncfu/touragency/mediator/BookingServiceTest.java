package ru.ncfu.touragency.mediator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ncfu.touragency.dto.request.BookingRequest;
import ru.ncfu.touragency.dto.response.BookingResponse;
import ru.ncfu.touragency.dto.response.CountryResponse;
import ru.ncfu.touragency.dto.response.TourResponse;
import ru.ncfu.touragency.entity.Booking;
import ru.ncfu.touragency.entity.Country;
import ru.ncfu.touragency.entity.Tour;
import ru.ncfu.touragency.entity.User;
import ru.ncfu.touragency.entity.enums.BookingStatus;
import ru.ncfu.touragency.exception.AccessDeniedAppException;
import ru.ncfu.touragency.exception.ResourceNotFoundException;
import ru.ncfu.touragency.foundation.BookingRepository;
import ru.ncfu.touragency.foundation.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookingServiceImpl — unit-тесты с Mockito")
class BookingServiceTest {

    @Mock BookingRepository bookingRepository;
    @Mock UserRepository userRepository;
    @Mock ITourService tourService;

    @InjectMocks BookingServiceImpl bookingService;

    private Tour tour;
    private User user;
    private TourResponse tourResponse;
    private CountryResponse countryResponse;

    @BeforeEach
    void setUp() {
        Country country = new Country("Турция", "Анталья", "http://img.com/turkey.jpg");
        setId(country, 1L);

        tour = new Tour();
        setId(tour, 10L);
        tour.setTitle("Анталья: пляжный отдых");
        tour.setPrice(new BigDecimal("45000"));
        tour.setAvailablePlaces(20);
        tour.setDurationDays(7);
        tour.setStartDate(LocalDate.now().plusDays(14));
        tour.setEndDate(LocalDate.now().plusDays(21));
        tour.setCountry(country);

        user = new User("Иван Иванов", "ivan@test.com", "pass", "+79001112233");
        setId(user, 5L);

        countryResponse = new CountryResponse(1L, "Турция", "Анталья", "http://img.com/turkey.jpg");
        tourResponse = new TourResponse(10L, "Анталья: пляжный отдых", null,
                new BigDecimal("45000"), 7,
                LocalDate.now().plusDays(14), LocalDate.now().plusDays(21),
                20, null, true, countryResponse, null, 0);
    }

    // ─── createBooking ────────────────────────────────────────────────

    @Nested
    @DisplayName("createBooking()")
    class CreateBooking {

        @Test
        @DisplayName("создаёт бронирование и возвращает ответ")
        void createsBookingSuccessfully() {
            BookingRequest request = new BookingRequest(2);
            when(tourService.getEntityOrThrow(10L)).thenReturn(tour);
            when(userRepository.findById(5L)).thenReturn(Optional.of(user));
            when(tourService.getById(10L)).thenReturn(tourResponse);

            Booking savedBooking = new Booking(user, tour, 2);
            setId(savedBooking, 100L);
            when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

            BookingResponse response = bookingService.createBooking(10L, 5L, request);

            assertThat(response).isNotNull();
            assertThat(response.numberOfPeople()).isEqualTo(2);
            assertThat(response.status()).isEqualTo(BookingStatus.PENDING.name());
            verify(bookingRepository).save(any(Booking.class));
        }

        @Test
        @DisplayName("бросает ResourceNotFoundException, если тур не найден")
        void throwsWhenTourNotFound() {
            when(tourService.getEntityOrThrow(999L))
                    .thenThrow(new ResourceNotFoundException("Тур не найден, id=999"));

            assertThatThrownBy(() ->
                    bookingService.createBooking(999L, 5L, new BookingRequest(1)))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("бросает ResourceNotFoundException, если пользователь не найден")
        void throwsWhenUserNotFound() {
            when(tourService.getEntityOrThrow(10L)).thenReturn(tour);
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    bookingService.createBooking(10L, 999L, new BookingRequest(1)))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("999");
        }

        @Test
        @DisplayName("бросает IllegalStateException, когда мест не хватает")
        void throwsWhenNotEnoughPlaces() {
            tour.setAvailablePlaces(0);
            when(tourService.getEntityOrThrow(10L)).thenReturn(tour);
            when(userRepository.findById(5L)).thenReturn(Optional.of(user));

            assertThatThrownBy(() ->
                    bookingService.createBooking(10L, 5L, new BookingRequest(1)))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    // ─── getMyBookings ────────────────────────────────────────────────

    @Test
    @DisplayName("getMyBookings() возвращает список бронирований пользователя")
    void getMyBookings_returnsUserBookings() {
        Booking booking = new Booking(user, tour, 1);
        setId(booking, 1L);
        when(bookingRepository.findByUserIdOrderByBookingDateDesc(5L))
                .thenReturn(List.of(booking));
        when(tourService.getById(10L)).thenReturn(tourResponse);

        List<BookingResponse> result = bookingService.getMyBookings(5L);

        assertThat(result).hasSize(1);
        verify(bookingRepository).findByUserIdOrderByBookingDateDesc(5L);
    }

    // ─── getById ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("getById()")
    class GetById {

        @Test
        @DisplayName("владелец может видеть своё бронирование")
        void ownerCanSeeOwnBooking() {
            Booking booking = new Booking(user, tour, 1);
            setId(booking, 1L);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
            when(tourService.getById(10L)).thenReturn(tourResponse);

            BookingResponse result = bookingService.getById(1L, 5L, false);
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("администратор может видеть чужое бронирование")
        void adminCanSeeAnyBooking() {
            Booking booking = new Booking(user, tour, 1);
            setId(booking, 1L);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
            when(tourService.getById(10L)).thenReturn(tourResponse);

            // requesterId=999 — другой пользователь, но isAdmin=true
            BookingResponse result = bookingService.getById(1L, 999L, true);
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("посторонний пользователь получает AccessDeniedAppException")
        void strangerGetsForbidden() {
            Booking booking = new Booking(user, tour, 1);
            setId(booking, 1L);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

            assertThatThrownBy(() -> bookingService.getById(1L, 999L, false))
                    .isInstanceOf(AccessDeniedAppException.class);
        }

        @Test
        @DisplayName("бросает ResourceNotFoundException для несуществующего id")
        void throwsWhenNotFound() {
            when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookingService.getById(999L, 5L, false))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ─── getByConfirmationCode ────────────────────────────────────────

    @Test
    @DisplayName("getByConfirmationCode() возвращает бронирование по коду")
    void getByConfirmationCode_found() {
        Booking booking = new Booking(user, tour, 1);
        setId(booking, 1L);
        String code = booking.getConfirmationCode();
        when(bookingRepository.findByConfirmationCode(code)).thenReturn(Optional.of(booking));
        when(tourService.getById(10L)).thenReturn(tourResponse);

        BookingResponse result = bookingService.getByConfirmationCode(code);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("getByConfirmationCode() бросает ResourceNotFoundException при неверном коде")
    void getByConfirmationCode_notFound() {
        when(bookingRepository.findByConfirmationCode("WRONG")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getByConfirmationCode("WRONG"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── cancelBooking ────────────────────────────────────────────────

    @Nested
    @DisplayName("cancelBooking()")
    class CancelBooking {

        @Test
        @DisplayName("владелец может отменить своё бронирование")
        void ownerCanCancel() {
            Booking booking = new Booking(user, tour, 2);
            setId(booking, 1L);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

            bookingService.cancelBooking(1L, 5L, false);

            assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        }

        @Test
        @DisplayName("администратор может отменить чужое бронирование")
        void adminCanCancelAny() {
            Booking booking = new Booking(user, tour, 1);
            setId(booking, 1L);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

            bookingService.cancelBooking(1L, 999L, true);

            assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        }

        @Test
        @DisplayName("посторонний пользователь получает AccessDeniedAppException")
        void strangerGetsForbidden() {
            Booking booking = new Booking(user, tour, 1);
            setId(booking, 1L);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

            assertThatThrownBy(() -> bookingService.cancelBooking(1L, 999L, false))
                    .isInstanceOf(AccessDeniedAppException.class);
        }
    }

    // ─── confirmBooking ───────────────────────────────────────────────

    @Test
    @DisplayName("confirmBooking() меняет статус на CONFIRMED")
    void confirmBooking_changesStatus() {
        Booking booking = new Booking(user, tour, 1);
        setId(booking, 1L);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(tourService.getById(10L)).thenReturn(tourResponse);

        BookingResponse result = bookingService.confirmBooking(1L);

        assertThat(result.status()).isEqualTo(BookingStatus.CONFIRMED.name());
    }

    // ─── getAll ───────────────────────────────────────────────────────

    @Test
    @DisplayName("getAll() возвращает все бронирования")
    void getAll_returnsAllBookings() {
        Booking b1 = new Booking(user, tour, 1);
        Booking b2 = new Booking(user, tour, 2);
        setId(b1, 1L); setId(b2, 2L);
        when(bookingRepository.findAll()).thenReturn(List.of(b1, b2));
        when(tourService.getById(10L)).thenReturn(tourResponse);

        List<BookingResponse> result = bookingService.getAll();

        assertThat(result).hasSize(2);
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
