package com.senla.resource_server.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorDto {
    private ErrorStatus errorStatus;
    private String errorMessage;

    public enum ErrorStatus {
        CLIENT_ERROR,
        SERVER_ERROR
    }
}
