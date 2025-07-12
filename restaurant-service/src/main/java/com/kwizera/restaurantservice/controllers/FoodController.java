package com.kwizera.restaurantservice.controllers;

import com.kwizera.restaurantservice.domain.dtos.FoodDTO;
import com.kwizera.restaurantservice.domain.entities.Food;
import com.kwizera.restaurantservice.domain.mappers.EntityToDTO;
import com.kwizera.restaurantservice.services.FoodServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/foods")
public class FoodController {
    private final FoodServices foodServices;

    @GetMapping("/{id}")
    public ResponseEntity<FoodDTO> getFood(@PathVariable Long id) {
        Optional<Food> food = foodServices.findFood(id);
        return food.map(value -> new ResponseEntity<>(
                EntityToDTO.foodEntityToDto(value),
                HttpStatus.OK
        )).orElseGet(() -> new ResponseEntity<>(
                null,
                HttpStatus.NOT_FOUND
        ));

    }
}
