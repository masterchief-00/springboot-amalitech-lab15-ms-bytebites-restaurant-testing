package com.kwizera.restaurantservice.integration;

import com.kwizera.restaurantservice.domain.entities.Restaurant;
import com.kwizera.restaurantservice.repositories.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@Transactional
public class RestaurantServiceIT {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("testDB")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RestaurantRepository restaurantRepository;
    private final Long userId = 1L;

    @Test
    void shouldCreateAndReturnRestaurant() throws Exception {
        String jsonPayload = """
                {
                    "ownerId": 1,
                    "name": "Restaurant 1",
                    "location": "Location xyz",
                    "description": "This is a restaurant located in xyz",
                    "menu": [
                        {
                            "name": "Pizza",
                            "category": "italian",
                            "price": 2000
                        },
                        {
                            "name": "Noodles",
                            "category": "asian",
                            "price": 700
                        }
                    ]
                }
                """;
        mockMvc.perform(post("/restaurant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload)
                        .header("X-User-Id", userId)
                        .header("X-User-Role", "OWNER")
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Restaurant 1"))
                .andExpect(jsonPath("$.menu.length()").value(2));
    }

    @Test
    void shouldRetrieveAllRestaurants() throws Exception {
        restaurantRepository.saveAll(List.of(
                        Restaurant.builder()
                                .name("A")
                                .description("description A")
                                .location("loc a")
                                .ownerId(1L)
                                .menu(Set.of())
                                .build(),
                        Restaurant.builder()
                                .name("B")
                                .description("description B")
                                .location("loc b")
                                .ownerId(1L)
                                .menu(Set.of())
                                .build()
                )
        );

        mockMvc.perform(get("/restaurant"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldRetrieveRestaurantById() throws Exception {
        restaurantRepository.save(Restaurant.builder()
                .name("Restaurant 1")
                .description("description A")
                .location("loc a")
                .ownerId(1L)
                .menu(Set.of())
                .build());

        mockMvc.perform(get("/restaurant/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Restaurant 1"))
                .andExpect(jsonPath("$.description").value("description A"));
    }
}
