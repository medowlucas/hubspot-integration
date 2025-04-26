package com.example.hubspotintegration.exception;

public class InvalidSignatureException extends RuntimeException {
    public InvalidSignatureException(String message) {
        super(message);
    }
    public InvalidSignatureException(String message, Throwable cause) {
        super(message, cause);
    }
}
