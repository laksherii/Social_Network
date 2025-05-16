package com.senla.resource_server.service.dto.user;

import com.senla.resource_server.data.entity.User.GenderType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchDto {

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Invalid email format")
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    private String lastName;

    @Positive(message = "Age must be a positive number")
    @Min(value = 0, message = "Age cannot be negative")
    @Max(value = 130, message = "Age cannot be greater than 130")
    private Integer age;

    @NotNull(message = "Gender must not be null")
    private GenderType gender;
}
