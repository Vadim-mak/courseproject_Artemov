package ru.ncfu.touragency.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ncfu.touragency.entity.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сущность Entity-слоя (PCMEF). Бронирование тура пользователем.
 */
@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 36)
    private String confirmationCode;

    @Column(nullable = false)
    private LocalDateTime bookingDate = LocalDateTime.now();

    @Column(nullable = false)
    private Integer numberOfPeople;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status = BookingStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    public Booking(User user, Tour tour, int numberOfPeople) {
        this.user = user;
        this.tour = tour;
        this.numberOfPeople = numberOfPeople;
        this.totalPrice = tour.calculateTotalPrice(numberOfPeople);
        this.confirmationCode = UUID.randomUUID().toString();
        this.status = BookingStatus.PENDING;
    }

    /* ---------- бизнес-методы ---------- */

    public void confirm() {
        if (this.status != BookingStatus.PENDING) {
            throw new IllegalStateException("Подтвердить можно только бронирование в статусе PENDING");
        }
        this.status = BookingStatus.CONFIRMED;
    }

    public void cancel() {
        if (this.status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Бронирование уже отменено");
        }
        this.tour.releasePlaces(this.numberOfPeople);
        this.status = BookingStatus.CANCELLED;
    }

    public boolean belongsTo(Long userId) {
        return this.user.getId().equals(userId);
    }
}
