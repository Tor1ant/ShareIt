package ru.practicum.shareit.exceptions;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionsHandler {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse badRequestExceptionHandler(BadRequestException badRequestException) {
        log.warn(badRequestException.getMessage());
        return new ErrorResponse(badRequestException.getMessage());
    }

    @RequiredArgsConstructor
    @Data
    private class ErrorResponse {

        private final String error;
        private String description;
    }
}
