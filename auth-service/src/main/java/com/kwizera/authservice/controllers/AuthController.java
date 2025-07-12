package com.kwizera.authservice.controllers;

import com.kwizera.authservice.domain.dtos.LoginRequestDTO;
import com.kwizera.authservice.domain.dtos.LoginResponseDTO;
import com.kwizera.authservice.domain.dtos.RegisterRequestDTO;
import com.kwizera.authservice.services.UserServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final UserServices userServices;

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        String token = userServices.register(request);
        return new ResponseEntity<>(
                LoginResponseDTO.builder()
                        .message("User registered")
                        .token(token)
                        .build(),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        String token = userServices.login(request);

        return new ResponseEntity<>(
                LoginResponseDTO.builder()
                        .message("Login successful")
                        .token(token)
                        .build(),
                HttpStatus.OK
        );
    }
}
