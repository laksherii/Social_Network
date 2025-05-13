package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.UserDao;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.data.entity.User.GenderType;
import com.senla.resource_server.data.entity.User.RoleType;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.exception.IllegalStateException;
import com.senla.resource_server.service.dto.user.CreateUserDtoRequest;
import com.senla.resource_server.service.dto.user.CreateUserDtoResponse;
import com.senla.resource_server.service.dto.user.UpdateUserDtoRequest;
import com.senla.resource_server.service.dto.user.UpdateUserDtoResponse;
import com.senla.resource_server.service.dto.user.UserDto;
import com.senla.resource_server.service.dto.user.UserInfoDto;
import com.senla.resource_server.service.dto.user.UserSearchDto;
import com.senla.resource_server.service.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setEnabled(true);
    }

    @Test
    void findById_WhenUserExists_ShouldReturnUserDto() {
        // given
        UserDto expectedDto = new UserDto("test@example.com");
        when(userDao.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(expectedDto);

        // when
        UserDto result = userService.findById(1L);

        // then
        assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    void findById_WhenUserNotFound_ShouldThrowException() {
        // given
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> userService.findById(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findByEmail_WhenUserExists_ShouldReturnUserDto() {
        // given
        when(userDao.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        UserDto expectedDto = new UserDto("test@example.com");
        when(userMapper.toUserDto(user)).thenReturn(expectedDto);

        // when
        UserDto result = userService.findByEmail("test@example.com");

        // then
        assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    void findByEmail_WhenUserNotFound_ShouldThrowException() {
        // given
        when(userDao.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> userService.findByEmail("test@example.com"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User with email test@example.com not found");
    }

    @Test
    void getUserInfo_WhenUserExists_ShouldReturnInfoDto() {
        // given
        UserInfoDto expectedDto = new UserInfoDto();
        expectedDto.setEmail("test@example.com");
        when(userDao.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        when(userMapper.toUserInfoDto(user)).thenReturn(expectedDto);

        // when
        UserInfoDto result = userService.getUserInfo("test@example.com");

        // then
        assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    void create_WhenRoleIsNull_ShouldNotOverrideRole() {
        // given
        CreateUserDtoRequest userDtoRequest = CreateUserDtoRequest.builder()
                .email("artem@example.com")
                .birthDay(LocalDate.of(2001, 1, 13))
                .lastName("Artem")
                .lastName("Lashkevich")
                .build();

        User mapped = new User();
        mapped.setId(1L);
        mapped.setEmail("artem@example.com");
        mapped.setRole(null);

        CreateUserDtoResponse createUserDtoResponse = new CreateUserDtoResponse();
        createUserDtoResponse.setEmail(mapped.getEmail());

        when(userDao.findByEmail(userDtoRequest.getEmail())).thenReturn(Optional.empty());
        when(userMapper.toUserCreate(userDtoRequest)).thenReturn(mapped);
        when(userDao.save(mapped)).thenReturn(mapped);
        when(userMapper.toCreateUserDtoResponse(mapped)).thenReturn(createUserDtoResponse);

        // when
        CreateUserDtoResponse response = userService.create(userDtoRequest);

        //then
        assertThat(response.getEmail()).isEqualTo("artem@example.com");
        assertThat(mapped.getRole()).isNull();
    }

    @Test
    void getUserInfo_WhenUserNotFound_ShouldThrowException() {
        // given
        when(userDao.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> userService.getUserInfo("test@example.com"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void create_WhenEmailIsUnique_ShouldReturnResponse() {
        // given
        CreateUserDtoRequest request = CreateUserDtoRequest.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .birthDay(LocalDate.of(1990, 1, 1))
                .gender(GenderType.MALE)
                .build();

        User newUser = User.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(RoleType.ROLE_USER)
                .birthDay(LocalDate.of(1990, 1, 1))
                .gender(GenderType.MALE)
                .build();

        User savedUser = new User();
        savedUser.setId(10L);
        savedUser.setEmail("test@example.com");

        CreateUserDtoResponse expected = CreateUserDtoResponse.builder()
                .email("test@example.com")
                .build();

        when(userDao.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userMapper.toUserCreate(request)).thenReturn(newUser);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userDao.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toCreateUserDtoResponse(savedUser)).thenReturn(expected);

        // when
        CreateUserDtoResponse result = userService.create(request);

        // then
        assertThat(result.getEmail()).isEqualTo(expected.getEmail());
    }

    @Test
    void create_WhenEmailExists_ShouldThrowException() {
        // given
        CreateUserDtoRequest request = CreateUserDtoRequest.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .birthDay(LocalDate.of(1990, 1, 1))
                .gender(GenderType.MALE)
                .build();
        when(userDao.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // when / then
        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void update_WhenUserExists_ShouldReturnUpdatedDto() {
        // given
        UpdateUserDtoRequest request = UpdateUserDtoRequest.builder()
                .email("updated@example.com")
                .firstName("UpdatedName")
                .lastName("UpdatedLastName")
                .build();

        User updated = User.builder()
                .email("updated@example.com")
                .firstName("UpdatedName")
                .lastName("UpdatedLastName")
                .build();

        UserDto userDto = new UserDto();
        userDto.setEmail("updated@example.com");

        UpdateUserDtoResponse expected = new UpdateUserDtoResponse();
        expected.setUser(userDto);

        when(userDao.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(userDao.update(user)).thenReturn(updated);
        when(userMapper.updateUserResponseToDto(updated)).thenReturn(expected);

        // when
        UpdateUserDtoResponse result = userService.update(request);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void update_WhenOnlyFirstNameProvided_ShouldUpdateFirstName() {
        // given
        UpdateUserDtoRequest request = UpdateUserDtoRequest.builder()
                .email("test@example.com")
                .firstName("NewFirstName")
                .build();

        user.setFirstName("OldFirstName");

        User updatedUser = new User();
        updatedUser.setFirstName("NewFirstName");

        UpdateUserDtoResponse expected = new UpdateUserDtoResponse();
        expected.setUser(new UserDto("test@example.com"));

        when(userDao.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userDao.update(any(User.class))).thenReturn(updatedUser);
        when(userMapper.updateUserResponseToDto(any(User.class))).thenReturn(expected);

        // when
        UpdateUserDtoResponse result = userService.update(request);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void update_WhenOnlyLastNameProvided_ShouldUpdateLastName() {
        // given
        UpdateUserDtoRequest request = UpdateUserDtoRequest.builder()
                .email("test@example.com")
                .lastName("NewLastName")
                .build();

        user.setLastName("OldLastName");

        User updatedUser = new User();
        updatedUser.setLastName("NewLastName");

        UpdateUserDtoResponse expected = new UpdateUserDtoResponse();
        expected.setUser(new UserDto("test@example.com"));

        when(userDao.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userDao.update(any(User.class))).thenReturn(updatedUser);
        when(userMapper.updateUserResponseToDto(any(User.class))).thenReturn(expected);

        // when
        UpdateUserDtoResponse result = userService.update(request);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void update_WhenOnlyGenderProvided_ShouldUpdateGender() {
        // given
        UpdateUserDtoRequest request = UpdateUserDtoRequest.builder()
                .email("test@example.com")
                .gender(GenderType.MALE)
                .build();

        user.setGender(GenderType.FEMALE);

        User updatedUser = new User();
        updatedUser.setGender(GenderType.MALE);

        UpdateUserDtoResponse expected = new UpdateUserDtoResponse();
        expected.setUser(new UserDto("test@example.com"));

        when(userDao.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userDao.update(any(User.class))).thenReturn(updatedUser);
        when(userMapper.updateUserResponseToDto(any(User.class))).thenReturn(expected);

        // when
        UpdateUserDtoResponse result = userService.update(request);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void update_WhenOnlyBirthDayProvided_ShouldUpdateBirthDay() {
        // given
        LocalDate newBirthDay = LocalDate.of(1995, 5, 15);
        UpdateUserDtoRequest request = UpdateUserDtoRequest.builder()
                .email("test@example.com")
                .birthDay(newBirthDay)
                .build();

        user.setBirthDay(LocalDate.of(1990, 1, 1));

        User updatedUser = new User();
        updatedUser.setBirthDay(newBirthDay);

        UpdateUserDtoResponse expected = new UpdateUserDtoResponse();
        expected.setUser(new UserDto("test@example.com"));

        when(userDao.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userDao.update(any(User.class))).thenReturn(updatedUser);
        when(userMapper.updateUserResponseToDto(any(User.class))).thenReturn(expected);

        // when
        UpdateUserDtoResponse result = userService.update(request);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void update_WhenUserNotFound_ShouldThrowException() {
        // given
        UpdateUserDtoRequest request = UpdateUserDtoRequest.builder()
                .email("test@example.com")
                .password("newPassword123")
                .birthDay(LocalDate.of(1995, 5, 15))
                .build();

        when(userDao.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> userService.update(request))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void searchUsers_WhenFound_ShouldReturnUserDtoList() {
        // given
        UserSearchDto searchDto = new UserSearchDto();
        UserDto expectedDto = new UserDto();
        expectedDto.setEmail("test@example.com");

        when(userDao.searchUser(searchDto)).thenReturn(List.of(user));
        when(userMapper.toUserDto(user)).thenReturn(expectedDto);

        // when
        List<UserDto> result = userService.searchUsers(searchDto);

        // then
        assertThat(result).containsExactly(expectedDto);
    }
}

