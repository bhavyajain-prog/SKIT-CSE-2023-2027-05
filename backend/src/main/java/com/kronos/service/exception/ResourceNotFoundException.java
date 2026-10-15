package com.kronos.service.exception;

/**
 * Thrown by services when a requested resource does not exist. Mapped to 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resource, Object id) {
        this(resource + " not found with id: " + id);
    }
}
