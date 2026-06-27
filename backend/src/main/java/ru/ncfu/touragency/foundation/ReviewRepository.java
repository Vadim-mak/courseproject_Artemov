package ru.ncfu.touragency.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ncfu.touragency.entity.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByTourIdOrderByCreatedAtDesc(Long tourId);
    boolean existsByUserIdAndTourId(Long userId, Long tourId);
}
