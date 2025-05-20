package com.senla.auth_server.service.impl;

import com.senla.auth_server.data.dao.UserDaoImpl;
import com.senla.auth_server.data.entity.User;
import com.senla.auth_server.data.entity.User.GenderType;
import com.senla.auth_server.exception.EmailAlreadyExistsException;
import com.senla.auth_server.service.dto.UserDtoRequest;
import com.senla.auth_server.service.dto.UserDtoResponse;
import com.senla.auth_server.service.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserDaoImpl userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    private UserDtoRequest request;
    private User user;
    private User savedUser;
    private UserDtoResponse expectedResponse;

    @BeforeEach
    void setUp() {
        request = UserDtoRequest.builder()
                .email("test@example.com")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .birthDay(LocalDate.of(1990, 1, 1))
                .gender(GenderType.MALE)
                .build();

        user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .birthDay(request.getBirthDay())
                .gender(request.getGender())
                .enabled(false)
                .build();

        savedUser = User.builder()
                .id(1L)
                .email(user.getEmail())
                .password("encodedPassword")
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDay(user.getBirthDay())
                .gender(user.getGender())
                .enabled(true)
                .build();

        expectedResponse = UserDtoResponse.builder()
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .birthDay(savedUser.getBirthDay())
                .gender(savedUser.getGender())
                .build();
    }

    @Test
    void create_shouldSaveUserAndReturnDto_whenEmailIsUnique() {
        // given
        when(userDao.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userMapper.toUserFromUserDtoRequest(request)).thenReturn(user);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userDao.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toUserDtoResponse(savedUser)).thenReturn(expectedResponse);

        // when
        UserDtoResponse actual = userService.create(request);

        // then
        assertThat(actual).isEqualTo(expectedResponse);
        verify(userDao).save(argThat(u ->
                u.getEmail().equals(request.getEmail()) &&
                u.getPassword().equals("encodedPassword") &&
                u.isEnabled()
        ));
    }

    @Test
    void create_shouldThrowException_whenEmailAlreadyExists() {
        // given
        when(userDao.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(new User()));

        // when / then
        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage("Email is already exist");

        verify(userDao, never()).save(any());
    }
}

