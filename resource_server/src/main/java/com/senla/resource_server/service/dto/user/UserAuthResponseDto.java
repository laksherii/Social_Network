package com.senla.resource_server.service.dto.user;

import com.senla.resource_server.data.entity.User.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@EqualsAndHashCode
@ToString
public class UserAuthResponseDto {

    @NotNull(message = "ID must not be null")
    private Long id;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Password must not be null")
    @Size(min = 6, message = "Password must be at least 4 characters long")
    private String password;

    @NotNull(message = "Role must not be null")
    private RoleType role;
}
