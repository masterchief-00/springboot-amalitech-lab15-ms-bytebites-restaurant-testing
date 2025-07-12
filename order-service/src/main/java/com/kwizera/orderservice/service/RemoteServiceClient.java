package com.kwizera.orderservice.service;

import com.kwizera.orderservice.domain.dtos.FoodDTO;
import com.kwizera.orderservice.domain.dtos.RestaurantDTO;
import com.kwizera.orderservice.domain.dtos.UserDTO;

public interface RemoteServiceClient {
    UserDTO fetchClient(Long clientId, String token);

    RestaurantDTO fetchRestaurant(Long restaurantId, String token);

    FoodDTO fetchFood(Long foodId, String token);
}
