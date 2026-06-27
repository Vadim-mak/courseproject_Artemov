package ru.ncfu.touragency.mediator;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ncfu.touragency.dto.request.TourRequest;
import ru.ncfu.touragency.dto.response.CountryResponse;
import ru.ncfu.touragency.dto.response.PageResponse;
import ru.ncfu.touragency.dto.response.TourResponse;
import ru.ncfu.touragency.entity.Country;
import ru.ncfu.touragency.entity.Tour;
import ru.ncfu.touragency.exception.ResourceNotFoundException;
import ru.ncfu.touragency.foundation.CountryRepository;
import ru.ncfu.touragency.foundation.ReviewRepository;
import ru.ncfu.touragency.foundation.TourRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Mediator-слой (PCMEF) — управление каталогом туров.
 */
@Service
@RequiredArgsConstructor
public class TourServiceImpl implements ITourService {

    private final TourRepository tourRepository;
    private final CountryRepository countryRepository;
    private final ReviewRepository reviewRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TourResponse> getAll(Pageable pageable) {
        Page<TourResponse> page = tourRepository.findByActiveTrue(pageable)
                .map(this::toResponse);
        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TourResponse> search(String keyword, Long countryId,
                                              BigDecimal minPrice, BigDecimal maxPrice,
                                              Pageable pageable) {
        Page<TourResponse> page = tourRepository.search(keyword, countryId, minPrice, maxPrice, pageable)
                .map(this::toResponse);
        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public TourResponse getById(Long id) {
        return toResponse(getEntityOrThrow(id));
    }

    @Override
    @Transactional
    public TourResponse create(TourRequest request) {
        Country country = countryRepository.findById(request.countryId())
                .orElseThrow(() -> new ResourceNotFoundException("Страна не найдена, id=" + request.countryId()));
        Tour tour = new Tour();
        fillFromRequest(tour, request, country);
        return toResponse(tourRepository.save(tour));
    }

    @Override
    @Transactional
    public TourResponse update(Long id, TourRequest request) {
        Tour tour = getEntityOrThrow(id);
        Country country = countryRepository.findById(request.countryId())
                .orElseThrow(() -> new ResourceNotFoundException("Страна не найдена, id=" + request.countryId()));
        fillFromRequest(tour, request, country);
        return toResponse(tour);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Tour tour = getEntityOrThrow(id);
        tour.deactivate();
    }

    @Override
    @Transactional(readOnly = true)
    public Tour getEntityOrThrow(Long id) {
        return tourRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Тур не найден, id=" + id));
    }

    private void fillFromRequest(Tour tour, TourRequest r, Country country) {
        tour.setTitle(r.title());
        tour.setDescription(r.description());
        tour.setPrice(r.price());
        tour.setDurationDays(r.durationDays());
        tour.setStartDate(r.startDate());
        tour.setEndDate(r.endDate());
        tour.setAvailablePlaces(r.availablePlaces());
        tour.setImageUrl(r.imageUrl());
        tour.setCountry(country);
    }

    private TourResponse toResponse(Tour t) {
        List<ru.ncfu.touragency.entity.Review> reviews = reviewRepository.findByTourIdOrderByCreatedAtDesc(t.getId());
        double avgRating = reviews.stream()
                .mapToInt(ru.ncfu.touragency.entity.Review::getRating)
                .average()
                .orElse(0.0);
        CountryResponse countryResponse = new CountryResponse(
                t.getCountry().getId(), t.getCountry().getName(),
                t.getCountry().getDescription(), t.getCountry().getImageUrl());
        return new TourResponse(
                t.getId(), t.getTitle(), t.getDescription(), t.getPrice(),
                t.getDurationDays(), t.getStartDate(), t.getEndDate(),
                t.getAvailablePlaces(), t.getImageUrl(), t.isActive(),
                countryResponse,
                reviews.isEmpty() ? null : avgRating,
                reviews.size()
        );
    }
}
