package ru.ncfu.touragency.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Сущность Entity-слоя (PCMEF). Тур, предлагаемый агентством.
 */
@Entity
@Table(name = "tours")
@Getter
@Setter
@NoArgsConstructor
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 3000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer durationDays;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Integer availablePlaces;

    @Column(length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    /* ---------- бизнес-методы ---------- */

    /** Проверяет, можно ли забронировать указанное число мест. */
    public boolean hasAvailablePlaces(int requestedPlaces) {
        return active && availablePlaces >= requestedPlaces;
    }

    /** Резервирует места под бронирование. Бросает исключение, если мест не хватает. */
    public void reservePlaces(int count) {
        if (!hasAvailablePlaces(count)) {
            throw new IllegalStateException("Недостаточно свободных мест в туре \"" + title + "\"");
        }
        this.availablePlaces -= count;
    }

    /** Возвращает места обратно в случае отмены бронирования. */
    public void releasePlaces(int count) {
        this.availablePlaces += count;
    }

    /** Полная стоимость для заданного числа туристов. */
    public BigDecimal calculateTotalPrice(int peopleCount) {
        return price.multiply(BigDecimal.valueOf(peopleCount));
    }

    public void deactivate() {
        this.active = false;
    }
}
