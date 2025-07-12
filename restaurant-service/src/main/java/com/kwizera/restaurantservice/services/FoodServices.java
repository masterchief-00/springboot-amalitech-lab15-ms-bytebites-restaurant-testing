package com.kwizera.restaurantservice.services;

import com.kwizera.restaurantservice.domain.entities.Food;

import java.util.Optional;

public interface FoodServices {
    Optional<Food> findFood(Long id);
}
