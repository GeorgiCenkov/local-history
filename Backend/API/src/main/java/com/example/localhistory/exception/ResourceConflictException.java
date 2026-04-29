package com.example.localhistory.exception;

/**
 * Thrown when a creation request conflicts with an already-existing resource
 * (e.g. registering with an email that is already in use).
 * Maps to HTTP 409 Conflict via GlobalExceptionHandler.
 */
public class ResourceConflictException extends RuntimeException {
    public ResourceConflictException(String message) {
        super(message);
    }
}