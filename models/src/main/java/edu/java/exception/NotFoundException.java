package edu.java.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    private final String message;
    private final String description;

    public NotFoundException(String message, String description) {
        this.message = message;
        this.description = description;
    }
}
