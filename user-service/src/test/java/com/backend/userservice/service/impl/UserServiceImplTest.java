package com.backend.userservice.service.impl;

import com.backend.userservice.dto.UserDTO;
import com.backend.userservice.exception.UserNotFoundException;
import com.backend.userservice.infrastructure.client.UserClient;
import com.backend.userservice.infrastructure.repository.UserRepository;
import com.backend.userservice.mapper.UserMapper;
import com.backend.userservice.util.UserMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserClient userClient;
    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Should sync users successfully when API returns data")
    void syncUsers_ShouldFetchAndSave() {
        // Given
        var dto = UserMock.createDto();
        var entity = UserMock.createEntity();

        when(userClient.fetchUsers()).thenReturn(List.of(dto));
        when(userMapper.toEntity(dto)).thenReturn(entity);

        // When
        userService.syncUsers();

        // Then
        verify(userClient, times(1)).fetchUsers();
        verify(userRepository, times(1)).saveAll(List.of(entity));
    }

    @Test
    @DisplayName("Should return all users when data exists")
    void getAllUsers_ShouldReturnList() {
        // Given
        var entity = UserMock.createEntity();
        var dto = UserMock.createDto();

        when(userRepository.findAll()).thenReturn(List.of(entity));
        when(userMapper.toDto(entity)).thenReturn(dto);

        // When
        List<UserDTO> result = userService.getAllUsers();

        // Then
        assertThat(result)
                .hasSize(1)
                .containsExactly(dto);
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Should return user when ID exists")
    void getUserById_ShouldReturnUser_WhenExists() {
        // Given
        Long id = 1L;
        var entity = UserMock.createEntity();
        var dto = UserMock.createDto();

        when(userRepository.findById(id)).thenReturn(Optional.of(entity));
        when(userMapper.toDto(entity)).thenReturn(dto);

        // When
        UserDTO result = userService.getUserById(id);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo(dto.name());
        verify(userRepository).findById(id);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when ID does not exist")
    void getUserById_ShouldThrowException_WhenNotFound() {
        Long id = 99L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(id))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Usuario no encontrado con ID: " + id);
    }
}