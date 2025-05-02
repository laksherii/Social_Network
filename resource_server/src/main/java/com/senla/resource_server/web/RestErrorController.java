package com.senla.resource_server.web;

import com.senla.resource_server.exception.BadRequestParamException;
import com.senla.resource_server.exception.EntityExistException;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.exception.IllegalArgumentException;
import com.senla.resource_server.exception.IllegalStateException;
import com.senla.resource_server.exception.UserNotAdminInGroupException;
import com.senla.resource_server.exception.UserNotInGroupChatException;
import com.senla.resource_server.service.dto.ErrorDto;
import com.senla.resource_server.service.dto.ErrorDto.ErrorStatus;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<String> errorMessages = bindingResult.getFieldErrors().stream()
                .map(this::getFieldErrorMessage)
                .collect(Collectors.toList());
        return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<String>> handleConstraintViolationExceptions(ConstraintViolationException ex) {
        List<String> errorMessages = ex.getConstraintViolations().stream()
                .map(violation -> String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
                .collect(Collectors.toList());
        return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);
    }

    public String getFieldErrorMessage(FieldError fieldError) {
        return String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage());
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorDto AuthorizationDeniedException(AuthorizationDeniedException ex) {
        log.error("Access Denied Exception", ex);
        return new ErrorDto(ErrorStatus.CLIENT_ERROR, ex.getMessage());
    }

    @ExceptionHandler(UserNotAdminInGroupException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDto UserNotAdminInGroup(UserNotAdminInGroupException ex) {
        log.error("User Not Admin Exception", ex);
        return new ErrorDto(ErrorStatus.CLIENT_ERROR, ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("Not Found Exception", ex);
        return new ErrorDto(ErrorStatus.CLIENT_ERROR, ex.getMessage());
    }

    @ExceptionHandler(BadRequestParamException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleBadRequestParamException(BadRequestParamException ex) {
        log.error("Bad Request Param Exception", ex);
        return new ErrorDto(ErrorStatus.CLIENT_ERROR, ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDto handleIllegalStateException(IllegalStateException ex) {
        log.error("Illegal State Exception", ex);
        return new ErrorDto(ErrorStatus.CLIENT_ERROR, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal Argument Exception", ex);
        return new ErrorDto(ErrorStatus.CLIENT_ERROR, ex.getMessage());
    }

    @ExceptionHandler(EntityExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDto handleEntityExistException(EntityExistException ex) {
        log.error("EntityExistException", ex);
        return new ErrorDto(ErrorStatus.CLIENT_ERROR, ex.getMessage());
    }

    @ExceptionHandler(UserNotInGroupChatException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDto handleUserNotInGroupChatException(UserNotInGroupChatException ex) {
        log.error("UserNotInGroupChatException", ex);
        return new ErrorDto(ErrorStatus.CLIENT_ERROR, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handleGenericException(Exception ex) {
        log.error("Unexpected error occurred ", ex);
        String message = "Oops! Something went wrong";
        return new ErrorDto(ErrorStatus.SERVER_ERROR, message);
    }
}
