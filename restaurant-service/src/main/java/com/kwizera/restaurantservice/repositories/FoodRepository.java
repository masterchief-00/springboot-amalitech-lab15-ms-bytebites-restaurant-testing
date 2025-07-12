package com.kwizera.restaurantservice.repositories;

import com.kwizera.restaurantservice.domain.entities.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
}
