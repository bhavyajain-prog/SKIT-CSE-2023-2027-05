package com.kronos.service.exception;

/**
 * Thrown by services when a request clashes with existing data
 * (duplicate unique key, row still referenced, ...). Mapped to 409.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
