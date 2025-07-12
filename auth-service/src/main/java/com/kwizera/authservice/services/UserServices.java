package com.kwizera.authservice.services;

import com.kwizera.authservice.domain.dtos.LoginRequestDTO;
import com.kwizera.authservice.domain.dtos.RegisterRequestDTO;
import com.kwizera.authservice.domain.entities.User;

import java.util.Optional;

public interface UserServices {
    String register(RegisterRequestDTO request);

    String login(LoginRequestDTO request);

    Optional<User> findUser(Long id);
}
