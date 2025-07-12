package com.kwizera.restaurantservice.services;

import com.kwizera.restaurantservice.domain.entities.Food;
import com.kwizera.restaurantservice.domain.entities.Restaurant;
import com.kwizera.restaurantservice.repositories.RestaurantRepository;
import com.kwizera.restaurantservice.services.impl.RestaurantServicesImpl;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class RestaurantServicesTest {

    @Mock
    private RestaurantRepository restaurantRepository;
    @InjectMocks
    private RestaurantServicesImpl restaurantServices;

    @Test
    void shouldCreateRestaurantWithFoodItems() {

        Restaurant restaurantInput = Restaurant.builder()
                .name("Restaurant test")
                .description("Test description")
                .location("district x, sector y")
                .ownerId(1L)
                .menu(getDummyFoods())
                .build();

        Restaurant restaurantSaved = Restaurant.builder()
                .id(1L)
                .name("Restaurant test")
                .description("Test description")
                .location("district x, sector y")
                .ownerId(1L)
                .menu(getDummyFoods())
                .build();

        when(restaurantRepository.save(restaurantInput)).thenReturn(restaurantSaved);
        Restaurant result = restaurantServices.create(restaurantInput);

        assertEquals("Restaurant test", result.getName());
        assertEquals(2, result.getMenu().size());

    }

    @Test
    void shouldThrowRunTimeExceptionIfRestaurantAlreadyExists() {

        Restaurant restaurantInputA = Restaurant.builder()
                .name("Restaurant test").build();

        Restaurant restaurantInputB = Restaurant.builder()
                .name("Restaurant test")
                .description("Test description")
                .location("district x, sector y")
                .ownerId(1L)
                .menu(getDummyFoods())
                .build();

        when(restaurantRepository.findByNameIgnoreCase("Restaurant test")).thenReturn(Optional.ofNullable(restaurantInputB));

        assertThrows(RuntimeException.class, () ->
                restaurantServices.create(restaurantInputA)
        );
    }

    @Test
    void shouldReturnAllRestaurants() {

        List<Restaurant> restaurantList = List.of(
                Restaurant.builder()
                        .id(1L)
                        .name("Restaurant A")
                        .description("Test description")
                        .location("district x, sector y")
                        .ownerId(1L)
                        .menu(getDummyFoods())
                        .build(),
                Restaurant.builder()
                        .id(2L)
                        .name("Restaurant B")
                        .description("Test description")
                        .location("district x, sector y")
                        .ownerId(1L)
                        .menu(getDummyFoods())
                        .build()
        );

        when(restaurantRepository.findAll()).thenReturn(restaurantList);

        List<Restaurant> result = restaurantServices.getRestaurants();
        assertEquals(2, result.size());
        assertEquals("Restaurant A", result.get(0).getName());
        assertEquals(2, result.get(0).getMenu().size());
        assertEquals("Restaurant B", result.get(1).getName());
        assertEquals(2, result.get(1).getMenu().size());
    }

    @Test
    void shouldReturnRestaurantById() {
        Restaurant dummyRestaurant = Restaurant.builder()
                .id(1L)
                .name("Restaurant A")
                .description("Test description")
                .location("district x, sector y")
                .ownerId(1L)
                .menu(getDummyFoods())
                .build();

        when(restaurantRepository.findById(1L)).thenReturn(Optional.ofNullable(dummyRestaurant));

        Optional<Restaurant> result = restaurantServices.findRestaurant(1L);

        assertTrue(result.isPresent());
        assertEquals("Restaurant A", result.get().getName());
        assertEquals(2, result.get().getMenu().size());
    }

    private Set<Food> getDummyFoods() {
        return Set.of(
                Food.builder()
                        .name("Food A")
                        .price(100.0)
                        .category("category a")
                        .build(),
                Food.builder()
                        .name("Food B")
                        .price(200.0)
                        .category("category b")
                        .build()
        );
    }
}
