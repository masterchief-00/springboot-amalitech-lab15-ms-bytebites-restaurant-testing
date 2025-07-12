package com.kwizera.authservice.controllers;

import com.kwizera.authservice.domain.dtos.UserDTO;
import com.kwizera.authservice.domain.entities.User;
import com.kwizera.authservice.domain.mappers.EntityToDTO;
import com.kwizera.authservice.services.UserServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserServices userServices;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId
    ) {
        if (Long.parseLong(userId) != id) throw new RuntimeException("Unauthorized access");

        Optional<User> user = userServices.findUser(id);

        if (user.isEmpty()) throw new UsernameNotFoundException("User not found");

        return new ResponseEntity<>(
                EntityToDTO.userEntityToDTO(user.get()),
                HttpStatus.OK
        );
    }
}
