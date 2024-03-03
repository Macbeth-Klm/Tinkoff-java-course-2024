package edu.java.exceptions;

import lombok.Getter;

@Getter
public class ScrapperNotFoundException extends RuntimeException {
    private final String description;

    public ScrapperNotFoundException(String message, String description) {
        super(message);
        this.description = description;
    }
}
