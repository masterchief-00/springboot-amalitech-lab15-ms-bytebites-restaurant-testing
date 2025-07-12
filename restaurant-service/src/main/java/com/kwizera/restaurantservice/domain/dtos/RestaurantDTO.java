package com.kwizera.restaurantservice.domain.dtos;

import lombok.Builder;

import java.util.List;

@Builder
public record RestaurantDTO(
        String name,
        String location,
        String description,
        List<String> menu
) {
}
