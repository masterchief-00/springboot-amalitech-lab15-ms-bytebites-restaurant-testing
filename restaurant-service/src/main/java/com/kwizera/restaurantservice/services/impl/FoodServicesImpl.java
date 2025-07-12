package com.kwizera.restaurantservice.services.impl;

import com.kwizera.restaurantservice.domain.entities.Food;
import com.kwizera.restaurantservice.repositories.FoodRepository;
import com.kwizera.restaurantservice.services.FoodServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FoodServicesImpl implements FoodServices {
    private final FoodRepository foodRepository;

    @Override
    public Optional<Food> findFood(Long id) {
        return foodRepository.findById(id);
    }
}
