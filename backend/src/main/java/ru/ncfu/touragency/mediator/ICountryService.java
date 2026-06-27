package ru.ncfu.touragency.mediator;

import ru.ncfu.touragency.dto.request.CountryRequest;
import ru.ncfu.touragency.dto.response.CountryResponse;

import java.util.List;

public interface ICountryService {
    List<CountryResponse> getAll();
    CountryResponse getById(Long id);
    CountryResponse create(CountryRequest request);
    CountryResponse update(Long id, CountryRequest request);
    void delete(Long id);
}
