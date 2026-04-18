package com.backend.userservice.dto;

public record UserDTO(
        Long id,
        String name,
        String username,
        String email
) {}