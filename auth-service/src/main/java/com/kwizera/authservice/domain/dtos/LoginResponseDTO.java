package com.kwizera.authservice.domain.dtos;

import lombok.Builder;

@Builder
public record LoginResponseDTO(
        String message,
        String token
) {
}
