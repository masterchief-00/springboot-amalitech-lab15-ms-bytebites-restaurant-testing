package com.kwizera.orderservice.domain.dtos;

import java.util.List;

public record RestaurantDTO(
        String name,
        String location,
        String description,
        List<String> menu
) {
}
