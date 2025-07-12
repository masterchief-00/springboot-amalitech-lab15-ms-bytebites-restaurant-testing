package com.kwizera.restaurantservice.domain.dtos;

import lombok.Builder;

@Builder
public record ApiErrorResponse(
        int status,
        String message
) {
}
