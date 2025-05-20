package com.senla.resource_server.service.impl;

import com.senla.resource_server.data.dao.UserDao;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.data.entity.User.GenderType;
import com.senla.resource_server.data.entity.User.RoleType;
import com.senla.resource_server.exception.EntityExistException;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.service.dto.user.CreateUserDtoRequest;
import com.senla.resource_server.service.dto.user.CreateUserDtoResponse;
import com.senla.resource_server.service.dto.user.UpdateRoleUserDtoRequest;
import com.senla.resource_server.service.dto.user.UpdateRoleUserDtoResponse;
import com.senla.resource_server.service.dto.user.UpdateUserDtoRequest;
import com.senla.resource_server.service.dto.user.UserDto;
import com.senla.resource_server.service.dto.user.UserInfoDto;
import com.senla.resource_server.service.dto.user.UserSearchDto;
import com.senla.resource_server.service.dto.user.UserSearchDtoResponse;
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

    @Mock
    private AuthService authService;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .enabled(true)
                .build();
    }

    @Test
    void findById_WhenUserExists_ShouldReturnUserDto() {
        // given
        UserDto expectedDto = UserDto.builder()
                .email("test@example.com")
                .build();

        when(userDao.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(expectedDto);

        // when
        UserDto result = userService.findById(1L);

        // then
        assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    void findById_WhenUserNotFound_ShouldThrowException() {
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findByEmail_WhenUserExists_ShouldReturnUserDto() {
        when(userDao.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDto expectedDto = UserDto.builder()
                .email("test@example.com")
                .build();

        when(userMapper.toUserDto(user)).thenReturn(expectedDto);

        UserDto result = userService.findByEmail("test@example.com");

        assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    void findByEmail_WhenUserNotFound_ShouldThrowException() {
        when(userDao.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByEmail("test@example.com"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("test@example.com");
    }

    @Test
    void getUserInfo_WhenUserExists_ShouldReturnInfoDto() {
        UserInfoDto expectedDto = UserInfoDto.builder()
                .email("test@example.com")
                .build();

        when(userDao.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userMapper.toUserInfoDto(user)).thenReturn(expectedDto);

        UserInfoDto result = userService.getUserInfo("test@example.com");

        assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    void getUserInfo_WhenUserNotFound_ShouldThrowException() {
        when(userDao.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserInfo("test@example.com"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void create_WhenRoleIsNull_ShouldNotOverrideRole() {
        // given
        CreateUserDtoRequest userDtoRequest = CreateUserDtoRequest.builder()
                .email("artem@example.com")
                .birthDay(LocalDate.of(2001, 1, 13))
                .lastName("Lashkevich")
                .build();

        User mapped = User.builder()
                .id(1L)
                .email("artem@example.com")
                .role(RoleType.ROLE_USER)
                .build();

        CreateUserDtoResponse createUserDtoResponse = CreateUserDtoResponse.builder()
                .email(mapped.getEmail())
                .build();

        when(userDao.findByEmail(userDtoRequest.getEmail())).thenReturn(Optional.empty());
        when(userMapper.toUserCreate(userDtoRequest)).thenReturn(mapped);
        when(userDao.save(mapped)).thenReturn(mapped);
        when(userMapper.toCreateUserDtoResponse(mapped)).thenReturn(createUserDtoResponse);

        // when
        CreateUserDtoResponse response = userService.create(userDtoRequest);

        // then
        assertThat(response.getEmail()).isEqualTo("artem@example.com");
        assertThat(mapped.getRole()).isEqualTo(RoleType.ROLE_USER);
    }

    @Test
    void create_WhenEmailIsUnique_ShouldReturnResponse() {
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

        User savedUser = User.builder()
                .id(10L)
                .email("test@example.com")
                .build();

        CreateUserDtoResponse expected = CreateUserDtoResponse.builder()
                .email("test@example.com")
                .build();

        when(userDao.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userMapper.toUserCreate(request)).thenReturn(newUser);
        when(userDao.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toCreateUserDtoResponse(savedUser)).thenReturn(expected);

        CreateUserDtoResponse result = userService.create(request);

        assertThat(result.getEmail()).isEqualTo(expected.getEmail());
    }

    @Test
    void create_WhenEmailExists_ShouldThrowException() {
        CreateUserDtoRequest request = CreateUserDtoRequest.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .birthDay(LocalDate.of(1990, 1, 1))
                .gender(GenderType.MALE)
                .build();

        when(userDao.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(EntityExistException.class);
    }

    @Test
    void update_WhenUserExists_ShouldReturnUpdatedDto() {
        String email = "user@example.com";
        UpdateUserDtoRequest request = UpdateUserDtoRequest.builder()
                .firstName("NewFirst")
                .lastName("NewLast")
                .gender(User.GenderType.FEMALE)
                .birthDay(LocalDate.of(1990, 5, 10))
                .build();

        User user = new User();
        user.setId(1L);
        user.setEmail(email);

        when(authService.getCurrentUser()).thenReturn(user);
        when(userDao.update(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userMapper.toUserSearchDtoResponse(any())).thenReturn(
                UserSearchDtoResponse.builder()
                        .email(email)
                        .firstName("NewFirst")
                        .lastName("NewLast")
                        .gender(User.GenderType.FEMALE)
                        .birthDay(LocalDate.of(1990, 5, 10))
                        .build()
        );

        UserSearchDtoResponse response = userService.update(request);

        assertThat(response.getEmail()).isEqualTo(email);
        assertThat(response.getFirstName()).isEqualTo("NewFirst");
        assertThat(response.getLastName()).isEqualTo("NewLast");
        assertThat(response.getGender()).isEqualTo(User.GenderType.FEMALE);
        assertThat(response.getBirthDay()).isEqualTo(LocalDate.of(1990, 5, 10));
    }

    @Test
    void update_WhenOnlyFirstNameProvided_ShouldUpdateOnlyFirstName() {
        String email = "user@example.com";
        UpdateUserDtoRequest request = UpdateUserDtoRequest.builder()
                .firstName("NewName")
                .build();

        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setFirstName("OldName");

        when(authService.getCurrentUser()).thenReturn(user);
        when(userDao.update(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userMapper.toUserSearchDtoResponse(any())).thenReturn(
                UserSearchDtoResponse.builder()
                        .email(email)
                        .firstName("NewName")
                        .build()
        );

        UserSearchDtoResponse response = userService.update(request);

        assertThat(response.getFirstName()).isEqualTo("NewName");
    }

    @Test
    void update_WhenOnlyLastNameProvided_ShouldUpdateOnlyLastName() {
        String email = "user@example.com";
        UpdateUserDtoRequest request = UpdateUserDtoRequest.builder()
                .lastName("NewLast")
                .build();

        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setLastName("OldLast");

        when(authService.getCurrentUser()).thenReturn(user);
        when(userDao.update(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userMapper.toUserSearchDtoResponse(any())).thenReturn(
                UserSearchDtoResponse.builder()
                        .email(email)
                        .lastName("NewLast")
                        .build()
        );

        UserSearchDtoResponse response = userService.update(request);

        assertThat(response.getLastName()).isEqualTo("NewLast");
    }

    @Test
    void update_WhenOnlyGenderProvided_ShouldUpdateOnlyGender() {
        String email = "user@example.com";
        UpdateUserDtoRequest request = UpdateUserDtoRequest.builder()
                .gender(GenderType.FEMALE)
                .build();

        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setGender(User.GenderType.MALE);

        when(authService.getCurrentUser()).thenReturn(user);
        when(userDao.update(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userMapper.toUserSearchDtoResponse(any())).thenReturn(
                UserSearchDtoResponse.builder()
                        .email(email)
                        .gender(GenderType.FEMALE)
                        .build()
        );

        UserSearchDtoResponse response = userService.update(request);

        assertThat(response.getGender()).isEqualTo(GenderType.FEMALE);
    }

    @Test
    void update_WhenOnlyBirthDayProvided_ShouldUpdateOnlyBirthDay() {
        String email = "user@example.com";
        LocalDate newBirth = LocalDate.of(1995, 1, 1);
        UpdateUserDtoRequest request = UpdateUserDtoRequest.builder()
                .birthDay(newBirth)
                .build();

        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setBirthDay(LocalDate.of(1990, 1, 1));

        when(authService.getCurrentUser()).thenReturn(user);
        when(userDao.update(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userMapper.toUserSearchDtoResponse(any())).thenReturn(
                UserSearchDtoResponse.builder()
                        .email(email)
                        .birthDay(newBirth)
                        .build()
        );

        UserSearchDtoResponse response = userService.update(request);

        assertThat(response.getBirthDay()).isEqualTo(newBirth);
    }

    @Test
    void searchUsers_WhenUsersExist_ShouldReturnList() {
        String searchStr = "test";
        UserSearchDto request = UserSearchDto.builder()
                .firstName(searchStr)
                .build();

        User user1 = User.builder().email("test1@example.com").build();
        User user2 = User.builder().email("test2@example.com").build();

        UserSearchDtoResponse userSearchDtoResponse1 = UserSearchDtoResponse.builder().email("test1@example.com").build();
        UserSearchDtoResponse userSearchDtoResponse2 = UserSearchDtoResponse.builder().email("test2@example.com").build();

        when(userDao.searchUser(request)).thenReturn(List.of(user1, user2));
        when(userMapper.toUserSearchDtoResponse(user1)).thenReturn(userSearchDtoResponse1);
        when(userMapper.toUserSearchDtoResponse(user2)).thenReturn(userSearchDtoResponse2);


        List<UserSearchDtoResponse> result = userService.searchUsers(request);

        assertThat(result).hasSize(2);
    }

    @Test
    void searchUsers_WhenNoUsersFound_ShouldReturnEmptyList() {
        UserSearchDto request = UserSearchDto.builder()
                .build();

        when(userDao.searchUser(request)).thenReturn(List.of());

        List<UserSearchDtoResponse> result = userService.searchUsers(request);

        assertThat(result).isEmpty();
    }

    @Test
    void updateRole_WhenUserExists_ShouldReturnUpdatedDto() {
        // given
        String email = "john.doe@example.com";
        RoleType newRole = RoleType.ROLE_ADMIN;

        UpdateRoleUserDtoRequest request = UpdateRoleUserDtoRequest.builder()
                .email(email)
                .role(newRole)
                .build();

        User user = User.builder()
                .id(1L)
                .email(email)
                .role(RoleType.ROLE_USER)
                .build();

        User updatedUser = User.builder()
                .id(1L)
                .email(email)
                .role(newRole)
                .build();

        UpdateRoleUserDtoResponse expectedResponse = UpdateRoleUserDtoResponse.builder()
                .email(email)
                .role(newRole)
                .build();

        when(userDao.findByEmail(email)).thenReturn(Optional.of(user));
        when(userDao.update(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toUpdateRoleUserDtoResponse(updatedUser)).thenReturn(expectedResponse);

        // when
        UpdateRoleUserDtoResponse actualResponse = userService.updateRole(request);

        // then
        assertThat(actualResponse)
                .isNotNull()
                .extracting(UpdateRoleUserDtoResponse::getEmail, UpdateRoleUserDtoResponse::getRole)
                .containsExactly(email, newRole);
    }

    @Test
    void updateRole_WhenUserNotFound_ShouldThrowEntityNotFoundException() {
        // given
        String email = "notfound@example.com";
        RoleType role = RoleType.ROLE_ADMIN;

        UpdateRoleUserDtoRequest request = UpdateRoleUserDtoRequest.builder()
                .email(email)
                .role(role)
                .build();

        when(userDao.findByEmail(email)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> userService.updateRole(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found by email=" + email);
    }
}

