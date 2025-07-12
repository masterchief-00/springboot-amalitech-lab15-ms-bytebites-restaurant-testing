package com.kwizera.orderservice.domain.dtos;

import lombok.Builder;

import java.util.List;

@Builder
public record CreateOrderDTO(
        Long restaurantId,
        List<CreateOrderItemDTO> items
) {
}
