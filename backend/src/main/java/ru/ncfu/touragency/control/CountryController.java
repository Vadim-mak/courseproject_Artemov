package ru.ncfu.touragency.control;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ncfu.touragency.dto.request.CountryRequest;
import ru.ncfu.touragency.dto.response.CountryResponse;
import ru.ncfu.touragency.mediator.ICountryService;

import java.util.List;

/**
 * Control-слой (PCMEF). Справочник стран/направлений.
 * Чтение — публично, изменение — только ROLE_ADMIN (см. SecurityConfig).
 */
@RestController
@RequestMapping("/api/countries")
@RequiredArgsConstructor
@Tag(name = "Countries", description = "Страны и направления туров")
public class CountryController {

    private final ICountryService countryService;

    @GetMapping
    @Operation(summary = "Список всех направлений")
    public List<CountryResponse> getAll() {
        return countryService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить направление по id")
    public CountryResponse getById(@PathVariable Long id) {
        return countryService.getById(id);
    }

    @PostMapping
    @Operation(summary = "Создать направление (только администратор)")
    public ResponseEntity<CountryResponse> create(@Valid @RequestBody CountryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(countryService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить направление (только администратор)")
    public CountryResponse update(@PathVariable Long id, @Valid @RequestBody CountryRequest request) {
        return countryService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить направление (только администратор)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        countryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
