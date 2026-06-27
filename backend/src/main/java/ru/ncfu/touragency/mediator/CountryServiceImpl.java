package ru.ncfu.touragency.mediator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ncfu.touragency.dto.request.CountryRequest;
import ru.ncfu.touragency.dto.response.CountryResponse;
import ru.ncfu.touragency.entity.Country;
import ru.ncfu.touragency.exception.BusinessRuleException;
import ru.ncfu.touragency.exception.ResourceNotFoundException;
import ru.ncfu.touragency.foundation.CountryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements ICountryService {

    private final CountryRepository countryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CountryResponse> getAll() {
        return countryRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CountryResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional
    public CountryResponse create(CountryRequest request) {
        if (countryRepository.existsByNameIgnoreCase(request.name())) {
            throw new BusinessRuleException("Страна \"" + request.name() + "\" уже существует");
        }
        Country country = new Country(request.name(), request.description(), request.imageUrl());
        return toResponse(countryRepository.save(country));
    }

    @Override
    @Transactional
    public CountryResponse update(Long id, CountryRequest request) {
        Country country = findOrThrow(id);
        country.setName(request.name());
        country.setDescription(request.description());
        country.setImageUrl(request.imageUrl());
        return toResponse(country);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Country country = findOrThrow(id);
        countryRepository.delete(country);
    }

    private Country findOrThrow(Long id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Страна не найдена, id=" + id));
    }

    private CountryResponse toResponse(Country c) {
        return new CountryResponse(c.getId(), c.getName(), c.getDescription(), c.getImageUrl());
    }
}
