package ru.ncfu.touragency.mediator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ncfu.touragency.dto.request.ReviewRequest;
import ru.ncfu.touragency.dto.response.ReviewResponse;
import ru.ncfu.touragency.entity.Review;
import ru.ncfu.touragency.entity.Tour;
import ru.ncfu.touragency.entity.User;
import ru.ncfu.touragency.exception.AccessDeniedAppException;
import ru.ncfu.touragency.exception.BusinessRuleException;
import ru.ncfu.touragency.exception.ResourceNotFoundException;
import ru.ncfu.touragency.foundation.ReviewRepository;
import ru.ncfu.touragency.foundation.TourRepository;
import ru.ncfu.touragency.foundation.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements IReviewService {

    private final ReviewRepository reviewRepository;
    private final TourRepository tourRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getByTour(Long tourId) {
        return reviewRepository.findByTourIdOrderByCreatedAtDesc(tourId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public ReviewResponse create(Long tourId, Long userId, ReviewRequest request) {
        if (reviewRepository.existsByUserIdAndTourId(userId, tourId)) {
            throw new BusinessRuleException("Вы уже оставляли отзыв на этот тур");
        }
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Тур не найден, id=" + tourId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден, id=" + userId));

        Review review = new Review(user, tour, request.rating(), request.comment());
        return toResponse(reviewRepository.save(review));
    }

    @Override
    @Transactional
    public void delete(Long reviewId, Long requesterId, boolean isAdmin) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Отзыв не найден, id=" + reviewId));
        if (!isAdmin && !review.getUser().getId().equals(requesterId)) {
            throw new AccessDeniedAppException("Нет доступа к чужому отзыву");
        }
        reviewRepository.delete(review);
    }

    private ReviewResponse toResponse(Review r) {
        return new ReviewResponse(
                r.getId(), r.getRating(), r.getComment(),
                r.getCreatedAt(), r.getUser().getFullName(), r.getTour().getId()
        );
    }
}
