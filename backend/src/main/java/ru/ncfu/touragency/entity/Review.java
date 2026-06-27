package ru.ncfu.touragency.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Сущность Entity-слоя (PCMEF). Отзыв пользователя о туре.
 */
@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer rating; // 1..5

    @Column(length = 1000)
    private String comment;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    public Review(User user, Tour tour, Integer rating, String comment) {
        validateRating(rating);
        this.user = user;
        this.tour = tour;
        this.rating = rating;
        this.comment = comment;
    }

    private void validateRating(Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Оценка должна быть от 1 до 5");
        }
    }
}
