package ru.ncfu.touragency.mediator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ncfu.touragency.dto.request.CountryRequest;
import ru.ncfu.touragency.dto.response.CountryResponse;
import ru.ncfu.touragency.entity.Country;
import ru.ncfu.touragency.exception.BusinessRuleException;
import ru.ncfu.touragency.exception.ResourceNotFoundException;
import ru.ncfu.touragency.foundation.CountryRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CountryServiceImpl — unit-тесты с Mockito")
class CountryServiceTest {

    @Mock CountryRepository countryRepository;
    @InjectMocks CountryServiceImpl countryService;

    private Country turkey;

    @BeforeEach
    void setUp() {
        turkey = new Country("Турция", "Морские курорты", "http://img.com/turkey.jpg");
        setId(turkey, 1L);
    }

    // ─── getAll ───────────────────────────────────────────────────────

    @Test
    @DisplayName("getAll() возвращает список всех стран")
    void getAll_returnsCountryList() {
        Country egypt = new Country("Египет", "Пирамиды", "http://img.com/egypt.jpg");
        setId(egypt, 2L);
        when(countryRepository.findAll()).thenReturn(List.of(turkey, egypt));

        List<CountryResponse> result = countryService.getAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CountryResponse::name)
                .containsExactly("Турция", "Египет");
    }

    @Test
    @DisplayName("getAll() возвращает пустой список, если стран нет")
    void getAll_returnsEmptyList() {
        when(countryRepository.findAll()).thenReturn(List.of());
        assertThat(countryService.getAll()).isEmpty();
    }

    // ─── getById ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("getById()")
    class GetById {

        @Test
        @DisplayName("возвращает CountryResponse по существующему id")
        void returnsCountry_whenFound() {
            when(countryRepository.findById(1L)).thenReturn(Optional.of(turkey));

            CountryResponse response = countryService.getById(1L);

            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.name()).isEqualTo("Турция");
            assertThat(response.description()).isEqualTo("Морские курорты");
            assertThat(response.imageUrl()).isEqualTo("http://img.com/turkey.jpg");
        }

        @Test
        @DisplayName("бросает ResourceNotFoundException при отсутствии страны")
        void throwsWhenNotFound() {
            when(countryRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> countryService.getById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    // ─── create ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("создаёт страну и возвращает ответ")
        void createsCountrySuccessfully() {
            CountryRequest request = new CountryRequest("Греция", "Острова Эгейского моря", "http://img.com/greece.jpg");
            Country saved = new Country("Греция", "Острова Эгейского моря", "http://img.com/greece.jpg");
            setId(saved, 3L);

            when(countryRepository.existsByNameIgnoreCase("Греция")).thenReturn(false);
            when(countryRepository.save(any(Country.class))).thenReturn(saved);

            CountryResponse response = countryService.create(request);

            assertThat(response.name()).isEqualTo("Греция");
            assertThat(response.id()).isEqualTo(3L);
            verify(countryRepository).save(any(Country.class));
        }

        @Test
        @DisplayName("бросает BusinessRuleException при дублировании имени")
        void throwsWhenDuplicateName() {
            CountryRequest request = new CountryRequest("Турция", "Описание", null);
            when(countryRepository.existsByNameIgnoreCase("Турция")).thenReturn(true);

            assertThatThrownBy(() -> countryService.create(request))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("Турция");

            verify(countryRepository, never()).save(any());
        }
    }

    // ─── update ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("обновляет поля страны и возвращает актуальные данные")
        void updatesCountryFields() {
            when(countryRepository.findById(1L)).thenReturn(Optional.of(turkey));
            CountryRequest request = new CountryRequest("Турция (Анталья)", "Новое описание", "http://new.img");

            CountryResponse response = countryService.update(1L, request);

            assertThat(response.name()).isEqualTo("Турция (Анталья)");
            assertThat(response.description()).isEqualTo("Новое описание");
            assertThat(response.imageUrl()).isEqualTo("http://new.img");
        }

        @Test
        @DisplayName("бросает ResourceNotFoundException при обновлении несуществующей страны")
        void throwsWhenNotFound() {
            when(countryRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    countryService.update(99L, new CountryRequest("X", null, null)))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ─── delete ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("удаляет найденную страну")
        void deletesCountry() {
            when(countryRepository.findById(1L)).thenReturn(Optional.of(turkey));

            countryService.delete(1L);

            verify(countryRepository).delete(turkey);
        }

        @Test
        @DisplayName("бросает ResourceNotFoundException при удалении несуществующей страны")
        void throwsWhenNotFound() {
            when(countryRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> countryService.delete(99L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(countryRepository, never()).delete(any());
        }
    }

    // ─── вспомогательный метод ────────────────────────────────────────

    private static void setId(Object obj, Long id) {
        try {
            var field = obj.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(obj, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
