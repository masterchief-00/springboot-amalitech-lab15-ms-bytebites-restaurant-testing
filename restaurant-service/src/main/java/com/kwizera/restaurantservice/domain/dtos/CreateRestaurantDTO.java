package com.kwizera.restaurantservice.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@Builder
public record CreateRestaurantDTO(
        @NotNull(message = "Onwer id is required")
        Long ownerId,
        @Pattern(regexp = "^[A-Za-z0-9,. ]{2,50}$", message = "Name must contain only alphanumeric characters")
        String name,
        @Pattern(regexp = "^[A-Za-z0-9,. ]{2,50}$", message = "Invalid location")
        String location,
        @Size(max = 150)
        @NotBlank(message = "Restaurant description is required")
        String description,

        @Size(min = 1, message = "At least one food needs to be served")
        List<FoodDTO> menu
) {
}
