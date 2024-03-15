package edu.java.exceptions;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {
    private final String message;
    private final String description;

    public BadRequestException(String message, String description) {
        this.message = message;
        this.description = description;
    }
}
