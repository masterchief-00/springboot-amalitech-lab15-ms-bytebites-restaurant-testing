package com.kwizera.authservice.domain.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RegisterRequestDTO(
        @Pattern(regexp = "^[A-Za-z0-9 ]{2,50}$", message = "Name must contain only alphanumeric characters")
        String firstName,
        @Pattern(regexp = "^[A-Za-z0-9 ]{2,50}$", message = "Name must contain only alphanumeric characters")
        String lastName,
        @NotNull(message = "Invalid email")
        @Email(message = "Invalid email")
        String email,
        @Size(min = 8, message = "The needs to be at least 8 characters")
        String password
) {
}
