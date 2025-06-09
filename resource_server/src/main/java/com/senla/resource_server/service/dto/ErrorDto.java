package com.senla.resource_server.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ErrorDto {

    @NotNull(message = "Error status must not be null")
    private ErrorStatus errorStatus;

    @NotBlank(message = "Error message must not be blank")
    private String errorMessage;

    public enum ErrorStatus {
        CLIENT_ERROR,
        SERVER_ERROR
    }
}
