package com.senla.resource_server.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senla.resource_server.data.entity.User.GenderType;
import com.senla.resource_server.service.dto.user.CreateUserDtoRequest;
import com.senla.resource_server.service.dto.user.CreateUserDtoResponse;
import com.senla.resource_server.service.dto.user.UpdateUserDtoRequest;
import com.senla.resource_server.service.dto.user.UpdateUserDtoResponse;
import com.senla.resource_server.service.dto.user.UserDto;
import com.senla.resource_server.service.dto.user.UserInfoDto;
import com.senla.resource_server.service.dto.user.UserSearchDto;
import com.senla.resource_server.service.dto.wall.WallDto;
import com.senla.resource_server.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestUserController.class)
@WithMockUser(roles = "ADMIN")
class RestUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserServiceImpl userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getUserById_ShouldReturnUser_WhenValidId() throws Exception {
        // Given
        Long userId = 1L;
        UserDto expectedUser = new UserDto("user@example.com");

        given(userService.findById(userId))
                .willReturn(expectedUser);

        // When & Then
        mockMvc.perform(get("/user/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    void getUserByEmail_ShouldReturnUser_WhenValidEmail() throws Exception {
        // Given
        String email = "user@example.com";
        UserDto expectedUser = new UserDto(email);

        given(userService.findByEmail(email))
                .willReturn(expectedUser);

        // When & Then
        mockMvc.perform(get("/user/email/{email}", email)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void getUserInfo_ShouldReturnUserInfo_WhenValidEmail() throws Exception {
        // Given
        String email = "user@example.com";
        UserInfoDto expectedInfo = new UserInfoDto(
                email,
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                new WallDto()
        );

        given(userService.getUserInfo(email))
                .willReturn(expectedInfo);

        // When & Then
        mockMvc.perform(get("/user/info/{email}", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void createUser_ShouldReturnCreatedUser_WhenValidRequest() throws Exception {
        // Given
        CreateUserDtoRequest request = CreateUserDtoRequest.builder()
                .email("new@example.com")
                .firstName("Alice")
                .lastName("Smith")
                .birthDay(LocalDate.of(1995, 5, 15))
                .gender(GenderType.FEMALE)
                .build();

        CreateUserDtoResponse expectedResponse = CreateUserDtoResponse.builder()
                .email("new@example.com")
                .firstName("Alice")
                .lastName("Smith")
                .birthDay(LocalDate.of(1995, 5, 15))
                .build();

        given(userService.create(any(CreateUserDtoRequest.class)))
                .willReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.firstName").value("Alice"));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser_WhenValidRequest() throws Exception {
        // Given
        UpdateUserDtoRequest request = UpdateUserDtoRequest.builder()
                .email("user@example.com")
                .firstName("Updated")
                .lastName("Name")
                .build();

        UserDto userDto = new UserDto("user@example.com");
        UpdateUserDtoResponse expectedResponse = new UpdateUserDtoResponse(userDto);

        given(userService.update(any(UpdateUserDtoRequest.class)))
                .willReturn(expectedResponse);

        // When & Then
        mockMvc.perform(put("/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value("user@example.com"));
    }

    @Test
    void searchUsers_ShouldReturnUsers_WhenValidParameters() throws Exception {
        // Given
        UserDto user1 = new UserDto("user1@example.com");
        UserDto user2 = new UserDto("user2@example.com");

        given(userService.searchUsers(any(UserSearchDto.class)))
                .willReturn(List.of(user1, user2));

        // When & Then
        mockMvc.perform(get("/user/search")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("age", "30")
                        .param("gender", "MALE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("user1@example.com"))
                .andExpect(jsonPath("$[1].email").value("user2@example.com"));
    }
}

