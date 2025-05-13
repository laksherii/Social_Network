package com.senla.auth_server.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDtoRequest {

    @NotBlank(message = "Email must be not null")
    private String email;
    private String password;
    private String firstName;
    private String lastName;

    @Past(message = "Birth day must be a past date")
    private LocalDate birthDay;
}
