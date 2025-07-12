package com.kwizera.orderservice.domain.dtos;

public record FoodDTO(
        String name,
        String category,
        Double price
) {
}
