package com.kwizera.authservice.domain.mappers;

import com.kwizera.authservice.domain.dtos.UserDTO;
import com.kwizera.authservice.domain.entities.User;

public class EntityToDTO {
    public static UserDTO userEntityToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .names(user.getFirstName() + " " + user.getLastName())
                .build();
    }
}
