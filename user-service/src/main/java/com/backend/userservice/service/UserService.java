package com.backend.userservice.service;

import com.backend.userservice.dto.UserDTO;
import java.util.List;

public interface UserService {
    void syncUsers();
    List<UserDTO> getAllUsers();
    UserDTO getUserById(Long id);
}