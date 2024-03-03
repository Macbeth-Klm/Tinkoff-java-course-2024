package edu.java.exceptions;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final String message;
    private final String description;

    public ApiException(String message, String description) {
        this.message = message;
        this.description = description;
    }
}
