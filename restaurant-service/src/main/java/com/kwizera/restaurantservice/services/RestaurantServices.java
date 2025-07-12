package com.kwizera.restaurantservice.services;

import com.kwizera.restaurantservice.domain.entities.Restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantServices {
    Restaurant create(Restaurant restaurant);

    List<Restaurant> getRestaurants();

    Optional<Restaurant> findRestaurant(Long id);
}
