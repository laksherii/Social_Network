package com.senla.resource_server.web;

import com.senla.resource_server.exception.BadRequestParamException;
import com.senla.resource_server.exception.EntityExistException;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.exception.IllegalArgumentException;
import com.senla.resource_server.exception.IllegalStateException;
import com.senla.resource_server.exception.UserNotAdminInGroupException;
import com.senla.resource_server.exception.UserNotInGroupChatException;
import com.senla.resource_server.exception.UserNotYourFriendException;
import com.senla.resource_server.service.dto.ErrorDto;
import com.senla.resource_server.service.dto.ErrorDto.ErrorStatus;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class RestErrorController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage(), ex);
        BindingResult bindingResult = ex.getBindingResult();
        List<String> errorMessages = bindingResult.getFieldErrors().stream()
                .map(this::getFieldErrorMessage)
                .collect(Collectors.toList());
        return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);
    }


    private String getFieldErrorMessage(FieldError fieldError) {
        return String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<String>> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error(ex.getMessage(), ex);
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDto handleBadCredentialsException(BadCredentialsException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorDto(ErrorStatus.CLIENT_ERROR, ex.getMessage());
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorDto handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorDto(ErrorStatus.CLIENT_ERROR, ex.getMessage());
    }

    @ExceptionHandler(UserNotAdminInGroupException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDto handleUserNotAdminInGroupException(UserNotAdminInGroupException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorDto(ErrorStatus.CLIENT_ERROR, ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorDto(ErrorStatus.CLIENT_ERROR, ex.getMessage());
    }

    @ExceptionHandler(BadRequestParamException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleBadRequestParamException(BadRequestParamException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorDto(ErrorStatus.CLIENT_ERROR, ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDto handleIllegalStateException(IllegalStateException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorDto(ErrorStatus.CLIENT_ERROR, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorDto(ErrorStatus.CLIENT_ERROR, ex.getMessage());
    }

    @ExceptionHandler(EntityExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDto handleEntityExistException(EntityExistException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorDto(ErrorStatus.CLIENT_ERROR, ex.getMessage());
    }

    @ExceptionHandler(UserNotInGroupChatException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDto handleUserNotInGroupChatException(UserNotInGroupChatException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorDto(ErrorStatus.CLIENT_ERROR, ex.getMessage());
    }

    @ExceptionHandler(UserNotYourFriendException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorDto handleUserNotYourFriendException(UserNotYourFriendException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorDto(ErrorStatus.CLIENT_ERROR, ex.getMessage());
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handleGenericException(Exception ex) {
        log.error(ex.getMessage(), ex);
        String message = "Oops! Something went wrong";
        return new ErrorDto(ErrorStatus.SERVER_ERROR, message);
    }
}
