package com.senla.resource_server.service.dto.user;

import com.senla.resource_server.data.entity.User.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class UserAuthResponseDto {
    private Long id;
    private String email;
    private String password;
    private RoleType role;
}
