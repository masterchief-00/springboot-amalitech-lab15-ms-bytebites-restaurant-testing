package com.kwizera.restaurantservice.domain.mappers;

import com.kwizera.restaurantservice.domain.dtos.FoodDTO;
import com.kwizera.restaurantservice.domain.entities.Food;

public class DTOtoEntity {
    public static Food foodDTOtoEntity(FoodDTO foodDTO) {
        return Food.builder()
                .name(foodDTO.name())
                .category(foodDTO.category())
                .price(foodDTO.price())
                .build();
    }
}
