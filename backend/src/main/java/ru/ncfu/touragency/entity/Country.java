package ru.ncfu.touragency.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Сущность Entity-слоя (PCMEF). Страна/направление, для которого
 * туристическое агентство предлагает туры.
 */
@Entity
@Table(name = "countries")
@Getter
@Setter
@NoArgsConstructor
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(length = 500)
    private String imageUrl;

    public Country(String name, String description, String imageUrl) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }
}
