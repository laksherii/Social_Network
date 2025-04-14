package com.senla.social_network.service.dto.user;

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
public class CreateUserDtoResponse {
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate birthDay;
}
