package com.example.hubspotintegration.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Conta não encontrada: " + ex.getMessage());
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<String> handleConflict(ResourceConflictException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado: " + ex.getMessage());
    }

    @ExceptionHandler(InvalidSignatureException.class)
    public ResponseEntity<String> handleInvalidSignature(InvalidSignatureException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Assinatura inválida: " + ex.getMessage());
    }

}
