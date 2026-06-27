package ru.ncfu.touragency.dto.response;

public record CountryResponse(
        Long id,
        String name,
        String description,
        String imageUrl
) {}
