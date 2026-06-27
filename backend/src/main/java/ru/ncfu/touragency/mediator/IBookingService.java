package ru.ncfu.touragency.mediator;

import ru.ncfu.touragency.dto.request.BookingRequest;
import ru.ncfu.touragency.dto.response.BookingResponse;

import java.util.List;

public interface IBookingService {
    BookingResponse createBooking(Long tourId, Long userId, BookingRequest request);
    List<BookingResponse> getMyBookings(Long userId);
    BookingResponse getById(Long bookingId, Long requesterId, boolean isAdmin);
    BookingResponse getByConfirmationCode(String code);
    void cancelBooking(Long bookingId, Long requesterId, boolean isAdmin);
    BookingResponse confirmBooking(Long bookingId); // только администратор
    List<BookingResponse> getAll(); // только администратор
}
