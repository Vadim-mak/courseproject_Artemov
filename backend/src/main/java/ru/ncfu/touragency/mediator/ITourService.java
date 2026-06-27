package ru.ncfu.touragency.mediator;

import org.springframework.data.domain.Pageable;
import ru.ncfu.touragency.dto.request.TourRequest;
import ru.ncfu.touragency.dto.response.PageResponse;
import ru.ncfu.touragency.dto.response.TourResponse;
import ru.ncfu.touragency.entity.Tour;

import java.math.BigDecimal;

public interface ITourService {
    PageResponse<TourResponse> getAll(Pageable pageable);
    PageResponse<TourResponse> search(String keyword, Long countryId, BigDecimal minPrice,
                                       BigDecimal maxPrice, Pageable pageable);
    TourResponse getById(Long id);
    TourResponse create(TourRequest request);
    TourResponse update(Long id, TourRequest request);
    void delete(Long id);

    /** Используется Mediator-слоем бронирования для прямого доступа к сущности. */
    Tour getEntityOrThrow(Long id);
}
