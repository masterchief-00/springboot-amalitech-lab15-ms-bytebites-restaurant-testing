package com.kwizera.restaurantservice.domain.mappers;

import com.kwizera.restaurantservice.domain.dtos.FoodDTO;
import com.kwizera.restaurantservice.domain.dtos.RestaurantDTO;
import com.kwizera.restaurantservice.domain.entities.Food;
import com.kwizera.restaurantservice.domain.entities.Restaurant;

import java.util.stream.Collectors;

public class EntityToDTO {
    public static RestaurantDTO restaurantEntityToDto(Restaurant restaurant) {
        return RestaurantDTO.builder()
                .name(restaurant.getName())
                .description(restaurant.getDescription())
                .location(restaurant.getLocation())
                .menu(
                        restaurant.getMenu().stream().map(Food::getName).collect(Collectors.toList())
                )
                .build();
    }

    public static FoodDTO foodEntityToDto(Food food) {
        return FoodDTO.builder()
                .name(food.getName())
                .category(food.getCategory())
                .price(food.getPrice())
                .build();
    }
}
