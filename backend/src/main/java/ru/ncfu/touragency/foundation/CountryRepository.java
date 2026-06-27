package ru.ncfu.touragency.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ncfu.touragency.entity.Country;

public interface CountryRepository extends JpaRepository<Country, Long> {
    boolean existsByNameIgnoreCase(String name);
}
