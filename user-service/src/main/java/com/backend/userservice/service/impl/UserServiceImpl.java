package com.backend.userservice.service.impl;

import com.backend.userservice.dto.UserDTO;
import com.backend.userservice.exception.UserNotFoundException;
import com.backend.userservice.infrastructure.client.UserClient;
import com.backend.userservice.infrastructure.repository.UserRepository;
import com.backend.userservice.mapper.UserMapper;
import com.backend.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserClient userClient;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public void syncUsers() {
        log.info("Sincronización de usuarios iniciada.");
        List<UserDTO> dtos = userClient.fetchUsers();
        var entities = dtos.stream().map(userMapper::toEntity).toList();
        userRepository.saveAll(entities);
        log.info("Sincronización completada. Total guardados: {}", entities.size());
    }

    @Override
    public List<UserDTO> getAllUsers() {
        log.debug("Recuperando todos los usuarios de la base de datos.");
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserDTO getUserById(Long id) {
        log.debug("Buscando usuario con ID: {}", id);
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("Intento de búsqueda fallido. ID no encontrado: {}", id);
                    return new UserNotFoundException("Usuario no encontrado con ID: " + id);
                });
    }
}