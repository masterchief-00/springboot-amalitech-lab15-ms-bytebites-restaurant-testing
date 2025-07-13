package com.kwizera.orderservice.service.impl;

import com.kwizera.orderservice.domain.dtos.FoodDTO;
import com.kwizera.orderservice.domain.dtos.GatewayPrerequisites;
import com.kwizera.orderservice.domain.dtos.RestaurantDTO;
import com.kwizera.orderservice.domain.dtos.UserDTO;
import com.kwizera.orderservice.service.RemoteServiceClient;
import com.kwizera.orderservice.utils.HmacUtil;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class RemoteServiceClientImpl implements RemoteServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(RemoteServiceClientImpl.class);
    private final WebClient.Builder webClientBuilder;
    private final String sharedSecret = System.getenv("SHARED_SECRET");
    private final String jwtSecret = System.getenv("JWT_SECRET");
    private final Retry retry;
    private final CircuitBreaker circuitBreaker;

    public RemoteServiceClientImpl(WebClient.Builder webClientBuilder,
                                   CircuitBreakerRegistry circuitBreakerRegistry,
                                   RetryRegistry retryRegistry) {
        this.webClientBuilder = webClientBuilder;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("sharedService");
        this.retry = retryRegistry.retry("sharedService");
    }

    @Override
    public UserDTO fetchClient(Long clientId, String token) {
        GatewayPrerequisites gatewayPrerequisites = getGatewayHeaders(token);

        try {
            return webClientBuilder.build()
                    .get()
                    .uri("lb://AUTH-SERVICE/users/{id}", clientId)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .header("X-User-Id", String.valueOf(clientId))
                    .header("X-User-Role", gatewayPrerequisites.role())
                    .header("X-Internal-Auth", gatewayPrerequisites.hmac())
                    .header("X-Internal-Payload", gatewayPrerequisites.payload())
                    .retrieve()
                    .bodyToMono(UserDTO.class)
                    .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                    .transformDeferred(RetryOperator.of(retry))
                    .block();
        } catch (Exception ex) {
            return fallBackClient(clientId, ex);
        }
    }

    @Override
    public RestaurantDTO fetchRestaurant(Long restaurantId, String token) {
        GatewayPrerequisites gatewayPrerequisites = getGatewayHeaders(token);
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("lb://RESTAURANT-SERVICE/restaurant/{id}", restaurantId)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .header("X-User-Id", Long.toHexString(gatewayPrerequisites.userId()))
                    .header("X-User-Role", gatewayPrerequisites.role())
                    .header("X-Internal-Auth", gatewayPrerequisites.hmac())
                    .header("X-Internal-Payload", gatewayPrerequisites.payload())
                    .retrieve()
                    .bodyToMono(RestaurantDTO.class)
                    .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                    .transformDeferred(RetryOperator.of(retry))
                    .block();
        } catch (Exception ex) {
            return fallBackRestaurant(restaurantId, ex);
        }
    }

    @Override
    public FoodDTO fetchFood(Long foodId, String token) {
        GatewayPrerequisites gatewayPrerequisites = getGatewayHeaders(token);

        try {
            return webClientBuilder.build()
                    .get()
                    .uri("lb://RESTAURANT-SERVICE/foods/{id}", foodId)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .header("X-User-Id", Long.toHexString(gatewayPrerequisites.userId()))
                    .header("X-User-Role", gatewayPrerequisites.role())
                    .header("X-Internal-Auth", gatewayPrerequisites.hmac())
                    .header("X-Internal-Payload", gatewayPrerequisites.payload())
                    .retrieve()
                    .bodyToMono(FoodDTO.class)
                    .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                    .transformDeferred(RetryOperator.of(retry))
                    .block();
        } catch (Exception ex) {
            return fallBackFood(foodId, ex);
        }
    }

    private RestaurantDTO fallBackRestaurant(Long restaurantId, Throwable ex) {
        logger.warn("Fallback triggered for restaurantId=" + restaurantId + " because: " + ex.getMessage());
        List<String> emptyList = new ArrayList<>();
        return new RestaurantDTO("Unknown restaurant", "Unknown location", "n/a", emptyList);
    }

    private FoodDTO fallBackFood(Long foodId, Throwable ex) {
        logger.warn("Fallback triggered for foodId=" + foodId + " because: " + ex.getMessage());
        return new FoodDTO("Unknown food", "unknown category", 0.0);
    }

    private UserDTO fallBackClient(Long clientId, Throwable ex) {
        logger.warn("Fallback triggered for userId=" + clientId + " because: " + ex.getMessage());
        return new UserDTO(0L, "unknown client name");
    }

    private GatewayPrerequisites getGatewayHeaders(String token) {
        Claims claims = getTokenClaims(token);

        Long userId = claims.get("id", Long.class);
        String role = claims.get("role", String.class);

        Instant timestamp = Instant.now();
        String payload = userId + ":" + role + ":" + timestamp;
        String hmac = HmacUtil.generateHmacSha256(payload, sharedSecret);
        return GatewayPrerequisites.builder()
                .hmac(hmac)
                .payload(payload)
                .role(role)
                .userId(userId)
                .build();
    }

    private Claims getTokenClaims(String bearerToken) {
        String[] bearerTokenArr = bearerToken.split("\\s");
        String token = bearerTokenArr[1];
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
