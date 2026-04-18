package com.backend.userservice.infrastructure.client;

import com.backend.userservice.dto.UserDTO;
import com.backend.userservice.exception.ExternalServiceException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.List;

@Slf4j
@Component
public class UserClient {

    @Value("${api.users.url}")
    private String baseUrl;

    private RestClient restClient;

    @PostConstruct
    public void init() {
        this.restClient = RestClient.create(baseUrl);
    }

    public List<UserDTO> fetchUsers() {
        log.info("Iniciando solicitud a API externa...");
        try {
            List<UserDTO> users = restClient.get()
                    .uri("/users")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<UserDTO>>() {});
            log.info("Solicitud exitosa. Se obtuvieron {} usuarios.", users != null ? users.size() : 0);
            return users;
        } catch (Exception e) {
            log.error("Error al conectar con JSONPlaceholder: {}", e.getMessage());
            throw new ExternalServiceException("Error al conectar con JSONPlaceholder: " + e.getMessage());
        }
    }
}