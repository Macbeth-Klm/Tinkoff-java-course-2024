package edu.java.api.exceptionhandlers;

import edu.java.exceptions.ScrapperInvalidReqException;
import edu.java.exceptions.ScrapperNotFoundException;
import edu.java.models.ApiErrorResponse;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    private final String apiErrorCode = "400";

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ScrapperInvalidReqException.class)
    public ApiErrorResponse handleValidationExceptions(@NotNull ScrapperInvalidReqException ex) {
        return new ApiErrorResponse(
            ex.getDescription(),
            apiErrorCode,
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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiErrorResponse handleInvalidUriException(HttpMessageNotReadableException ex) {
        return new ApiErrorResponse(
            "Некорректные параметры запроса",
            apiErrorCode,
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
        );
    }
}
