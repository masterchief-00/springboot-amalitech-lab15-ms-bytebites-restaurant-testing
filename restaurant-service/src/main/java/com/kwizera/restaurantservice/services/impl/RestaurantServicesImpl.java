package com.kwizera.restaurantservice.services.impl;

import com.kwizera.restaurantservice.domain.entities.Restaurant;
import com.kwizera.restaurantservice.repositories.RestaurantRepository;
import com.kwizera.restaurantservice.services.RestaurantServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RestaurantServicesImpl implements RestaurantServices {
    private final RestaurantRepository restaurantRepository;

    @Override
    public Restaurant create(Restaurant restaurant) {
        Optional<Restaurant> restaurantOptional = restaurantRepository.findByNameIgnoreCase(restaurant.getName());

        if (restaurantOptional.isPresent()) throw new RuntimeException("Restaurant with such name already exists");

        return restaurantRepository.save(restaurant);
    }

    @Override
    public List<Restaurant> getRestaurants() {
        return restaurantRepository.findAll();
    }

    @Override
    public Optional<Restaurant> findRestaurant(Long id) {
        return restaurantRepository.findById(id);
    }
}
