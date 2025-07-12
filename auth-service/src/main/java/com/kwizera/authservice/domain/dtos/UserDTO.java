package com.kwizera.authservice.domain.dtos;

import lombok.Builder;

@Builder
public record UserDTO(
        Long id,
        String names
) {
}
