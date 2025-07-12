package com.kwizera.orderservice.domain.dtos;

public record CreateOrderItemDTO(
        Long foodId,
        int quantity
) {
}
