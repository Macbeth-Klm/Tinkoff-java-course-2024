package edu.java.api.exceptionhandlers;

import edu.java.api.exceptions.BotApiException;
import edu.java.api.models.ApiErrorResponse;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BotApiException.class)
    public ApiErrorResponse handleValidationExceptions(BotApiException ex) {
        return new ApiErrorResponse(
            "Некорректные параметры запроса",
            "400",
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
        );
    }
}
