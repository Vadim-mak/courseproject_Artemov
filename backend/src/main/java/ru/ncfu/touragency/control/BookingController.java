package ru.ncfu.touragency.control;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.ncfu.touragency.dto.request.BookingRequest;
import ru.ncfu.touragency.dto.response.BookingResponse;
import ru.ncfu.touragency.mediator.IBookingService;
import ru.ncfu.touragency.security.SecurityUtils;

import java.util.List;

/**
 * Control-слой (PCMEF). Бронирования туров. Все эндпоинты, кроме
 * административных, требуют аутентификации (см. SecurityConfig).
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Бронирование туров")
public class BookingController {

    private final IBookingService bookingService;

    @PostMapping("/tour/{tourId}")
    @Operation(summary = "Забронировать тур")
    public ResponseEntity<BookingResponse> book(@PathVariable Long tourId,
                                                 @Valid @RequestBody BookingRequest request) {
        Long userId = SecurityUtils.currentUserId();
        BookingResponse response = bookingService.createBooking(tourId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    @Operation(summary = "Мои бронирования")
    public List<BookingResponse> myBookings() {
        return bookingService.getMyBookings(SecurityUtils.currentUserId());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Бронирование по id (владелец или администратор)")
    public BookingResponse getById(@PathVariable Long id) {
        return bookingService.getById(id, SecurityUtils.currentUserId(), SecurityUtils.isAdmin());
    }

    @GetMapping("/confirmation/{code}")
    @Operation(summary = "Найти бронирование по коду подтверждения")
    public BookingResponse getByConfirmationCode(@PathVariable String code) {
        return bookingService.getByConfirmationCode(code);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Отменить бронирование (владелец или администратор)")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        bookingService.cancelBooking(id, SecurityUtils.currentUserId(), SecurityUtils.isAdmin());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Подтвердить бронирование (только администратор)")
    public BookingResponse confirm(@PathVariable Long id) {
        return bookingService.confirmBooking(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Все бронирования (только администратор)")
    public List<BookingResponse> getAll() {
        return bookingService.getAll();
    }
}
