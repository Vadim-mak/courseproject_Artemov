package ru.ncfu.touragency.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ncfu.touragency.entity.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserIdOrderByBookingDateDesc(Long userId);
    Optional<Booking> findByConfirmationCode(String confirmationCode);
    List<Booking> findByTourId(Long tourId);
}
