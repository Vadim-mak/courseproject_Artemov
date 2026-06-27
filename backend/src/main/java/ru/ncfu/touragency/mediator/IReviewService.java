package ru.ncfu.touragency.mediator;

import ru.ncfu.touragency.dto.request.ReviewRequest;
import ru.ncfu.touragency.dto.response.ReviewResponse;

import java.util.List;

public interface IReviewService {
    List<ReviewResponse> getByTour(Long tourId);
    ReviewResponse create(Long tourId, Long userId, ReviewRequest request);
    void delete(Long reviewId, Long requesterId, boolean isAdmin);
}
