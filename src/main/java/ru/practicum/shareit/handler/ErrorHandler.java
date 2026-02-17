package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.exception.NotOwnerException;
import ru.practicum.shareit.user.exception.EmailAlreadyExistsException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException exception) {
        log.warn("Object is not found: {}", exception.getMessage());
        return ErrorResponse.builder().error(HttpStatus.NOT_FOUND.value()).description(exception.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailAlreadyExistsException(final EmailAlreadyExistsException exception) {
        log.warn("User with same Email already exists: {}", exception.getMessage());
        return ErrorResponse.builder().error(HttpStatus.CONFLICT.value()).description(exception.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleNotOwnerException(final NotOwnerException exception) {
        log.warn("User is not the owner of this item: {}", exception.getMessage());
        return ErrorResponse.builder().error(HttpStatus.CONFLICT.value()).description(exception.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException exception) {
        log.warn("Validation error: {}", exception.getMessage());
        return ErrorResponse.builder()
                .error(HttpStatus.BAD_REQUEST.value())
                .description(exception.getMessage())
                .build();
    }
}