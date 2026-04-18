package com.backend.userservice.infrastructure.controller;

import com.backend.userservice.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;

    // Endpoint rápido para obtener un token de prueba
    @GetMapping("/generate-token")
    public String generateToken(@RequestParam String username) {
        return jwtService.generateToken(username);
    }
}