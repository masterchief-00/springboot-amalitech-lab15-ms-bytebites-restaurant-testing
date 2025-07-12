package com.kwizera.authservice.domain.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record LoginRequestDTO(
        @Email(message = "Invalid email")
        String email,
        @Size(min = 8, message = "The needs to be at least 8 characters")
        String password
) {
}
