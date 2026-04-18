package com.backend.userservice.infrastructure.controller;

import com.backend.userservice.dto.UserDTO;
import com.backend.userservice.service.UserService;
import com.backend.userservice.util.UserMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.backend.userservice.infrastructure.security.JwtService;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) // Ignora seguridad para testear solo la lógica del controller
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // <--- Anotación correcta para 3.4+
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @DisplayName("POST /users/sync - Should trigger sync and return 200")
    void syncUsers_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/users/sync")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).syncUsers();
    }

    @Test
    @DisplayName("GET /users - Should return list of users")
    void getAllUsers_ShouldReturnList() throws Exception {
        // Given
        List<UserDTO> dtoList = UserMock.createDtoList();
        when(userService.getAllUsers()).thenReturn(dtoList);

        // When & Then
        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(dtoList.getFirst().name()))
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)));
    }

    @Test
    @DisplayName("GET /users/{id} - Should return user by id")
    void getUserById_ShouldReturnUser() throws Exception {
        // Given
        Long id = 1L;
        UserDTO dto = UserMock.createDto();
        when(userService.getUserById(id)).thenReturn(dto);

        // When & Then
        mockMvc.perform(get("/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dto.id()))
                .andExpect(jsonPath("$.name").value(dto.name()));
    }
}