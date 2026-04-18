package com.backend.userservice.exception;

import com.backend.userservice.exception.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Manejo específico para "No encontrado"
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        return buildResponse(ex.getMessage(), "USER_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    // 2. Manejo específico para fallos de servicios externos
    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternalService(ExternalServiceException ex) {
        log.error("Fallo al consumir servicio externo: {}", ex.getMessage());
        return buildResponse(ex.getMessage(), "EXTERNAL_SERVICE_ERROR", HttpStatus.BAD_GATEWAY);
    }

    // 3. Manejo genérico para cualquier otra cosa que no hayamos previsto
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        // Pasamos 'ex' como tercer parámetro para imprimir el Stack Trace completo en los logs
        log.error("Error inesperado en el sistema: {}", ex.getMessage(), ex);
        return buildResponse("Ocurrió un error inesperado", "INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Método auxiliar para evitar duplicidad de código
    private ResponseEntity<ErrorResponse> buildResponse(String message, String code, HttpStatus status) {
        ErrorResponse error = new ErrorResponse(message, code, LocalDateTime.now());
        return new ResponseEntity<>(error, status);
    }
}