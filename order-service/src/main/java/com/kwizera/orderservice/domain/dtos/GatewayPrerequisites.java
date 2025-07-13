package com.kwizera.orderservice.domain.dtos;

import lombok.Builder;

@Builder
public record GatewayPrerequisites(
        Long userId,
        String payload,
        String hmac,
        String role
) {
}
