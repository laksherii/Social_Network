package com.senla.resource_server.web;


import com.senla.resource_server.exception.BadRequestParamException;
import com.senla.resource_server.exception.EntityExistException;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.exception.IllegalArgumentException;
import com.senla.resource_server.exception.IllegalStateException;
import com.senla.resource_server.exception.UserNotInGroupChatException;
import com.senla.resource_server.service.dto.ErrorDto;
import com.senla.resource_server.service.dto.ErrorDto.ErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestErrorController {

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
