package com.kwizera.restaurantservice.controllers;

import com.kwizera.restaurantservice.domain.dtos.CreateRestaurantDTO;
import com.kwizera.restaurantservice.domain.dtos.RestaurantDTO;
import com.kwizera.restaurantservice.domain.entities.Restaurant;
import com.kwizera.restaurantservice.domain.mappers.DTOtoEntity;
import com.kwizera.restaurantservice.domain.mappers.EntityToDTO;
import com.kwizera.restaurantservice.exceptions.UnauthorizedAccessException;
import com.kwizera.restaurantservice.services.RestaurantServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/restaurant")
public class RestaurantController {
    private final RestaurantServices restaurantServices;

    @GetMapping
    public ResponseEntity<List<RestaurantDTO>> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantServices.getRestaurants();

        return new ResponseEntity<>(
                restaurants.stream().map(
                        EntityToDTO::restaurantEntityToDto
                ).collect(Collectors.toList()),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDTO> getRestaurant(@PathVariable Long id
    ) {
        Optional<Restaurant> restaurant = restaurantServices.findRestaurant(id);
        return restaurant.map(value -> new ResponseEntity<>(
                EntityToDTO.restaurantEntityToDto(value),
                HttpStatus.OK
        )).orElseGet(() -> new ResponseEntity<>(
                null,
                HttpStatus.NOT_FOUND
        ));

    }

    @PostMapping
    public ResponseEntity<RestaurantDTO> createRestaurant(
            @RequestBody CreateRestaurantDTO restaurantDetails,
            @RequestHeader("X-User-Role") String role
    ) throws UnauthorizedAccessException {
        if (!"OWNER".equals(role)) {
            throw new UnauthorizedAccessException("Only owners can create restaurants.");
        }

        Restaurant restaurant = Restaurant.builder()
                .ownerId(restaurantDetails.ownerId())
                .name(restaurantDetails.name())
                .description(restaurantDetails.description())
                .location(restaurantDetails.location())
                .menu(
                        restaurantDetails.menu().stream().map(
                                DTOtoEntity::foodDTOtoEntity
                        ).collect(Collectors.toSet())
                )
                .build();

        Restaurant createdRestaurant = restaurantServices.create(restaurant);

        return new ResponseEntity<>(EntityToDTO.restaurantEntityToDto(createdRestaurant), HttpStatus.CREATED);

    }
}
