package ru.ncfu.touragency.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.ncfu.touragency.entity.*;
import ru.ncfu.touragency.entity.enums.RoleName;
import ru.ncfu.touragency.foundation.CountryRepository;
import ru.ncfu.touragency.foundation.RoleRepository;
import ru.ncfu.touragency.foundation.TourRepository;
import ru.ncfu.touragency.foundation.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Наполняет базу демонстрационными данными при первом запуске:
 * роли, администратор по умолчанию, несколько стран и туров.
 * Полезно для защиты курсового проекта (Swagger UI сразу с данными).
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final TourRepository tourRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_USER)));
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_ADMIN)));

        if (!userRepository.existsByEmail("admin@touragency.local")) {
            User admin = new User("Администратор системы", "admin@touragency.local",
                    passwordEncoder.encode("Admin123!"), "+70000000000");
            admin.addRole(adminRole);
            admin.addRole(userRole);
            userRepository.save(admin);
        }

        if (countryRepository.count() == 0) {
            Country turkey = countryRepository.save(new Country("Турция",
                    "Морские курорты Антальи и Бодрума, all inclusive.", "https://images.unsplash.com/photo-1524231757912-21f4fe3a7200"));
            Country georgia = countryRepository.save(new Country("Грузия",
                    "Горы Кавказа, винные регионы Кахетии, старый Тбилиси.", "https://images.unsplash.com/photo-1565008576549-57569a49371d"));
            Country egypt = countryRepository.save(new Country("Египет",
                    "Красное море, дайвинг, древние пирамиды Гизы.", "https://images.unsplash.com/photo-1568322445389-f64ac2515020"));

            tourRepository.save(buildTour("Анталья: пляжный отдых", turkey,
                    new BigDecimal("45000"), 7, 25));
            tourRepository.save(buildTour("Тбилиси и Казбеги", georgia,
                    new BigDecimal("38000"), 5, 18));
            tourRepository.save(buildTour("Хургада: дайвинг-тур", egypt,
                    new BigDecimal("52000"), 10, 15));
        }
    }

    private Tour buildTour(String title, Country country, BigDecimal price, int days, int places) {
        Tour tour = new Tour();
        tour.setTitle(title);
        tour.setDescription("Демонстрационный тур, добавлен автоматически при первом запуске backend.");
        tour.setPrice(price);
        tour.setDurationDays(days);
        tour.setStartDate(LocalDate.now().plusDays(14));
        tour.setEndDate(LocalDate.now().plusDays(14 + days));
        tour.setAvailablePlaces(places);
        tour.setImageUrl(country.getImageUrl());
        tour.setCountry(country);
        return tour;
    }
}
