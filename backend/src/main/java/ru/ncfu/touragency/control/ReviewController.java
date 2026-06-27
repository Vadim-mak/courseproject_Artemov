package ru.ncfu.touragency.control;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ncfu.touragency.dto.request.ReviewRequest;
import ru.ncfu.touragency.dto.response.ReviewResponse;
import ru.ncfu.touragency.mediator.IReviewService;
import ru.ncfu.touragency.security.SecurityUtils;

import java.util.List;

/**
 * Control-слой (PCMEF). Отзывы туристов о турах.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Отзывы о турах")
public class ReviewController {

    private final IReviewService reviewService;

    @GetMapping("/reviews/tour/{tourId}")
    @Operation(summary = "Список отзывов о туре")
    public List<ReviewResponse> getByTour(@PathVariable Long tourId) {
        return reviewService.getByTour(tourId);
    }

    @PostMapping("/tours/{tourId}/reviews")
    @Operation(summary = "Оставить отзыв о туре")
    public ResponseEntity<ReviewResponse> create(@PathVariable Long tourId,
                                                  @Valid @RequestBody ReviewRequest request) {
        ReviewResponse response = reviewService.create(tourId, SecurityUtils.currentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/reviews/{id}")
    @Operation(summary = "Удалить отзыв (автор или администратор)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewService.delete(id, SecurityUtils.currentUserId(), SecurityUtils.isAdmin());
        return ResponseEntity.noContent().build();
    }
}
