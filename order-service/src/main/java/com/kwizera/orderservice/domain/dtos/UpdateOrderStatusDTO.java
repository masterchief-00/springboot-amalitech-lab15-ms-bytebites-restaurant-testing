package com.kwizera.orderservice.domain.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateOrderStatusDTO(
        @Min(1)
        @Max(4)
        int newStatus
) {
}
