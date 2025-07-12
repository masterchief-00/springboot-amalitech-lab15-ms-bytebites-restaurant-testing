package com.kwizera.restaurantservice.domain.dtos;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record FoodDTO(
        @Pattern(regexp = "^[A-Za-z ]{2,50}$", message = "Name must contain only alphanumeric characters")
        String name,

        @Pattern(regexp = "^[A-Za-z ]{2,50}$", message = "Name must contain only alphanumeric characters")
        String category,

        @Pattern(regexp = "^[0-9.]{2,50}$", message = "Invalid price")
        Double price
) {
}
