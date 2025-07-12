package com.kwizera.orderservice.service.impl;

import com.kwizera.orderservice.domain.dtos.FoodDTO;
import com.kwizera.orderservice.domain.dtos.RestaurantDTO;
import com.kwizera.orderservice.domain.dtos.UserDTO;
import com.kwizera.orderservice.service.RemoteServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RemoteServiceClientImpl implements RemoteServiceClient {
    private final WebClient.Builder webClientBuilder;

    @Override
    public UserDTO fetchClient(Long clientId, String token) {
        return webClientBuilder.build()
                .get()
                .uri("lb://AUTH-SERVICE/users/{id}", clientId)
                .header(HttpHeaders.AUTHORIZATION, token)
                .header("X-User-Id",String.valueOf(clientId))
                .retrieve()
                .bodyToMono(UserDTO.class)
                .onErrorReturn(new UserDTO(0L, "unknown client name"))
                .block();

    }

    @Override
    public RestaurantDTO fetchRestaurant(Long restaurantId, String token) {

        List<String> emptyList = new ArrayList<>();

        return webClientBuilder.build()
                .get()
                .uri("lb://RESTAURANT-SERVICE/restaurant/{id}", restaurantId)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(RestaurantDTO.class)
                .onErrorReturn(new RestaurantDTO("Unknown restaurant", "Unkown location", "n/a", emptyList))
                .block();
    }

    @Override
    public FoodDTO fetchFood(Long foodId, String token) {
        return webClientBuilder.build()
                .get()
                .uri("lb://RESTAURANT-SERVICE/foods/{id}", foodId)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(FoodDTO.class)
                .onErrorReturn(new FoodDTO("Unknown food", "unknown category", 0.0))
                .block();
    }
}
