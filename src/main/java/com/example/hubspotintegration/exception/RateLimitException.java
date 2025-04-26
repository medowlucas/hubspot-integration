package com.example.hubspotintegration.exception;

public class RateLimitException extends RuntimeException {
    public RateLimitException(String message) {
        super(message);
    }
}