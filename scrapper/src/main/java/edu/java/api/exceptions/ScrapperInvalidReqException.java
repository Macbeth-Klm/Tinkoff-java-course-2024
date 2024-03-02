package edu.java.api.exceptions;

import lombok.Getter;

@Getter
public class ScrapperInvalidReqException extends RuntimeException {
    private final String description;

    public ScrapperInvalidReqException(String message, String description) {
        super(message);
        this.description = description;
    }
}
