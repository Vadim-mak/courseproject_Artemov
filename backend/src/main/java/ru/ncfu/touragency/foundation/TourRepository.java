package ru.ncfu.touragency.foundation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ncfu.touragency.entity.Tour;

import java.math.BigDecimal;

public interface TourRepository extends JpaRepository<Tour, Long> {

    Page<Tour> findByActiveTrue(Pageable pageable);

    @Query("""
            select t from Tour t
            where t.active = true
              and (:countryId is null or t.country.id = :countryId)
              and (:minPrice is null or t.price >= :minPrice)
              and (:maxPrice is null or t.price <= :maxPrice)
              and (:keyword is null or lower(t.title) like lower(concat('%', :keyword, '%'))
                   or lower(t.description) like lower(concat('%', :keyword, '%')))
            """)
    Page<Tour> search(@Param("keyword") String keyword,
                       @Param("countryId") Long countryId,
                       @Param("minPrice") BigDecimal minPrice,
                       @Param("maxPrice") BigDecimal maxPrice,
                       Pageable pageable);
}
