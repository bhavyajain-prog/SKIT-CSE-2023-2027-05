package com.kronos.service.exception;

/** The request is well-formed but refers to something that does not exist (e.g. an unknown role name). */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
