package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.NotOwnerException;
import ru.practicum.shareit.user.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(final UserNotFoundException exception) {
        log.warn("User is not found: {}", exception.getMessage());
        return ErrorResponse.builder().error(HttpStatus.NOT_FOUND.value()).description(exception.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailAlreadyExistsException(final EmailAlreadyExistsException exception) {
        log.warn("User with same Email already exists: {}", exception.getMessage());
        return ErrorResponse.builder().error(HttpStatus.CONFLICT.value()).description(exception.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemNotFound(final ItemNotFoundException exception) {
        log.warn("Item is not found: {}", exception.getMessage());
        return ErrorResponse.builder().error(HttpStatus.NOT_FOUND.value()).description(exception.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleNotOwnerException(final NotOwnerException exception) {
        log.warn("User is not the owner of this item: {}", exception.getMessage());
        return ErrorResponse.builder().error(HttpStatus.CONFLICT.value()).description(exception.getMessage()).build();
    }
}