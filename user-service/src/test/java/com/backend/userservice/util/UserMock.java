package com.backend.userservice.util;

import com.backend.userservice.dto.UserDTO;
import com.backend.userservice.infrastructure.entity.UserEntity;

import java.util.List;

public final class UserMock {

    private UserMock() {}

    public static UserDTO createDto() {
        return new UserDTO(1L, "John Doe", "jdoe", "john@example.com");
    }

    public static UserEntity createEntity() {
        return UserEntity.builder()
                .id(1L)
                .name("John Doe")
                .username("jdoe")
                .email("john@example.com")
                .build();
    }

    // Métodos para generar listas
    public static List<UserDTO> createDtoList() {
        return List.of(createDto());
    }

    public static List<UserEntity> createEntityList() {
        return List.of(createEntity());
    }
}