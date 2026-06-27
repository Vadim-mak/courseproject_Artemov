package ru.ncfu.touragency.control;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ncfu.touragency.dto.request.TourRequest;
import ru.ncfu.touragency.dto.response.PageResponse;
import ru.ncfu.touragency.dto.response.TourResponse;
import ru.ncfu.touragency.mediator.ITourService;

import java.math.BigDecimal;

/**
 * Control-слой (PCMEF). Каталог туров: список, поиск/фильтрация,
 * CRUD для администратора.
 */
@RestController
@RequestMapping("/api/tours")
@RequiredArgsConstructor
@Tag(name = "Tours", description = "Каталог туров")
public class TourController {

    private final ITourService tourService;

    @GetMapping
    @Operation(summary = "Список активных туров (с пагинацией)")
    public PageResponse<TourResponse> getAll(Pageable pageable) {
        return tourService.getAll(pageable);
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск туров по ключевому слову, стране и диапазону цены")
    public PageResponse<TourResponse> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long countryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Pageable pageable) {
        return tourService.search(keyword, countryId, minPrice, maxPrice, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Детали тура по id")
    public TourResponse getById(@PathVariable Long id) {
        return tourService.getById(id);
    }

    @PostMapping
    @Operation(summary = "Создать тур (только администратор)")
    public ResponseEntity<TourResponse> create(@Valid @RequestBody TourRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tourService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить тур (только администратор)")
    public TourResponse update(@PathVariable Long id, @Valid @RequestBody TourRequest request) {
        return tourService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Деактивировать тур (только администратор, мягкое удаление)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tourService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
