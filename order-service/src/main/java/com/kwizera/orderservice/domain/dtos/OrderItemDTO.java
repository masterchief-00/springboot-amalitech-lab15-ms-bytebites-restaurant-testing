package com.kwizera.orderservice.domain.dtos;

import lombok.Builder;

@Builder
public record OrderItemDTO(
        String name,
        int quantity,
        double subTotal
) {
}
