package com.senla.auth_server.service.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Getter
@Setter
public class UserDtoToResourceServer {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate birthDay;
    private String role;
    private boolean enabled;
}
