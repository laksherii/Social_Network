package com.senla.resource_server.service.dto.user;

import com.senla.resource_server.data.entity.User.GenderType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class UpdateUserDtoRequest {

    @Email(message = "Invalid email format")
    private String email;

    private String password;
    private String firstName;
    private String lastName;
    private GenderType gender;

    @Past(message = "Birth day must be a past date")
    private LocalDate birthDay;
}
