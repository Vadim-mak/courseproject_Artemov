package ru.ncfu.touragency.mediator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ncfu.touragency.dto.request.BookingRequest;
import ru.ncfu.touragency.dto.response.BookingResponse;
import ru.ncfu.touragency.entity.Booking;
import ru.ncfu.touragency.entity.Tour;
import ru.ncfu.touragency.entity.User;
import ru.ncfu.touragency.exception.AccessDeniedAppException;
import ru.ncfu.touragency.exception.ResourceNotFoundException;
import ru.ncfu.touragency.foundation.BookingRepository;
import ru.ncfu.touragency.foundation.UserRepository;

import java.util.List;

/**
 * Mediator-слой (PCMEF) — управление бронированиями: проверка доступности
 * мест, расчёт стоимости, разграничение доступа "владелец/администратор",
 * управление транзакциями (требование методички).
 */
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements IBookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ITourService tourService;

    @Override
    @Transactional
    public BookingResponse createBooking(Long tourId, Long userId, BookingRequest request) {
        Tour tour = tourService.getEntityOrThrow(tourId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден, id=" + userId));

        // бизнес-правило проверяется на уровне Entity (Tour.reservePlaces)
        tour.reservePlaces(request.numberOfPeople());

        Booking booking = new Booking(user, tour, request.numberOfPeople());
        Booking saved = bookingRepository.save(booking);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(Long userId) {
        return bookingRepository.findByUserIdOrderByBookingDateDesc(userId).stream()
                .map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getById(Long bookingId, Long requesterId, boolean isAdmin) {
        Booking booking = findOrThrow(bookingId);
        ensureOwnerOrAdmin(booking, requesterId, isAdmin);
        return toResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getByConfirmationCode(String code) {
        Booking booking = bookingRepository.findByConfirmationCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Бронирование с таким кодом не найдено"));
        return toResponse(booking);
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId, Long requesterId, boolean isAdmin) {
        Booking booking = findOrThrow(bookingId);
        ensureOwnerOrAdmin(booking, requesterId, isAdmin);
        booking.cancel(); // вернёт места в Tour (см. Booking.cancel())
    }

    @Override
    @Transactional
    public BookingResponse confirmBooking(Long bookingId) {
        Booking booking = findOrThrow(bookingId);
        booking.confirm();
        return toResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getAll() {
        return bookingRepository.findAll().stream().map(this::toResponse).toList();
    }

    private Booking findOrThrow(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Бронирование не найдено, id=" + id));
    }

    private void ensureOwnerOrAdmin(Booking booking, Long requesterId, boolean isAdmin) {
        if (!isAdmin && !booking.belongsTo(requesterId)) {
            throw new AccessDeniedAppException("Нет доступа к чужому бронированию");
        }
    }

    private BookingResponse toResponse(Booking b) {
        return new BookingResponse(
                b.getId(), b.getConfirmationCode(), b.getBookingDate(), b.getNumberOfPeople(),
                b.getTotalPrice(), b.getStatus().name(), tourService.getById(b.getTour().getId()),
                b.getUser().getId(), b.getUser().getFullName()
        );
    }
}
