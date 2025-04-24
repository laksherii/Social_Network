package com.senla.resource_server.service.dto.user;

import com.senla.resource_server.data.entity.User.GenderType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class CreateUserDtoRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate birthDay;
    private GenderType gender;
}
