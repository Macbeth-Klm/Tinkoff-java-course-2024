package edu.java.bot.api.exceptionhandlers;

import edu.java.exceptions.BotApiException;
import edu.java.models.ApiErrorResponse;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    private final String apiErrorDescription = "Некорректные параметры запроса";
    private final String apiErrorCode = "400";

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BotApiException.class)
    public ApiErrorResponse handleValidationExceptions(BotApiException ex) {
        return new ApiErrorResponse(
            apiErrorDescription,
            apiErrorCode,
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiErrorResponse handleInvalidUriException(HttpMessageNotReadableException ex) {
        return new ApiErrorResponse(
            apiErrorDescription,
            apiErrorCode,
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
        );
    }
}
