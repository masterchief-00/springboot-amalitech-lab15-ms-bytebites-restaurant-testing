package com.kwizera.orderservice.domain.dtos;

import lombok.Builder;

import java.util.List;

@Builder
public record OrderDTO(
        Long id,
        String customer,
        String restaurant,
        Double totalPrice,
        List<OrderItemDTO> items
) {
}
