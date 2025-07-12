package com.kwizera.restaurantservice.services;

import com.kwizera.restaurantservice.domain.entities.Food;
import com.kwizera.restaurantservice.repositories.FoodRepository;
import com.kwizera.restaurantservice.services.impl.FoodServicesImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class FoodServicesTest {
    @Mock
    private FoodRepository foodRepository;
    @InjectMocks
    private FoodServicesImpl foodServices;

    @Test
    void shouldReturnFoodById() {
        Food food = Food.builder()
                .id(1L)
                .name("Food A")
                .price(100.0)
                .category("category a")
                .build();

        when(foodRepository.findById(1L)).thenReturn(Optional.ofNullable(food));

        Optional<Food> result = foodServices.findFood(1L);
        assertTrue(result.isPresent());
        assertEquals("Food A", result.get().getName());
    }
}
