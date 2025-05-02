package com.senla.auth_server.web;

import com.senla.auth_server.service.dto.ErrorDto;
import com.senla.auth_server.service.dto.ErrorDto.ErrorStatus;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestErrorController {

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDto handleBadCredentialsException(BadCredentialsException ex) {
    return new ErrorDto(ErrorStatus.CLIENT_ERROR, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handleGenericException(Exception ex) {
        return new ErrorDto(ErrorStatus.SERVER_ERROR, "An unexpected error occurred.");
    }
}
