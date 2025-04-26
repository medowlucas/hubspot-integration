package com.example.hubspotintegration.exception;

import org.springframework.http.HttpStatus;

public class ResourceConflictException extends RuntimeException {
    private final HttpStatus status = HttpStatus.CONFLICT;

    public ResourceConflictException(String message) {
        super(message);
    }

    public ResourceConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpStatus getStatus() {
        return status;
    }
}
