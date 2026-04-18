package com.backend.userservice.mapper;

import com.backend.userservice.dto.UserDTO;
import com.backend.userservice.infrastructure.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity toEntity(UserDTO dto);
    UserDTO toDto(UserEntity entity);
}