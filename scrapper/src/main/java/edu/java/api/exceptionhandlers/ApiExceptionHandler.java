package edu.java.api.exceptionhandlers;

import edu.java.api.exceptions.ScrapperInvalidReqException;
import edu.java.api.exceptions.ScrapperNotFoundException;
import edu.java.models.ApiErrorResponse;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ScrapperInvalidReqException.class)
    public ApiErrorResponse handleValidationExceptions(@NotNull ScrapperInvalidReqException ex) {
        return new ApiErrorResponse(
            ex.getDescription(),
            "400",
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
        );
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ScrapperNotFoundException.class)
    public ApiErrorResponse handleNotFoundExceptions(@NotNull ScrapperNotFoundException ex) {
        return new ApiErrorResponse(
            ex.getDescription(),
            "404",
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
        );
    }
}
