package com.senla.resource_server.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senla.resource_server.data.entity.User.GenderType;
import com.senla.resource_server.data.entity.User.RoleType;
import com.senla.resource_server.service.dto.message.WallMessageDto;
import com.senla.resource_server.service.dto.user.CreateUserDtoRequest;
import com.senla.resource_server.service.dto.user.CreateUserDtoResponse;
import com.senla.resource_server.service.dto.user.UpdateRoleUserDtoRequest;
import com.senla.resource_server.service.dto.user.UpdateRoleUserDtoResponse;
import com.senla.resource_server.service.dto.user.UpdateUserDtoRequest;
import com.senla.resource_server.service.dto.user.UserDto;
import com.senla.resource_server.service.dto.user.UserInfoDto;
import com.senla.resource_server.service.dto.user.UserSearchDtoResponse;
import com.senla.resource_server.service.dto.wall.WallDto;
import com.senla.resource_server.service.interfaces.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestUserController.class)
@Import(TestConfig.class)
class RestUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private UserDto createUserDto() {
        return UserDto.builder()
                .email("test@example.com")
                .build();
    }

    private UserInfoDto createUserInfoDto() {
        return UserInfoDto.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .birthDay(LocalDate.now().minusYears(20))
                .wall(WallDto.builder()
                        .messages(List.of(
                                WallMessageDto.builder()
                                        .content("Test message")
                                        .build()))
                        .build())
                .build();
    }

    private CreateUserDtoResponse createUserDtoResponse() {
        return CreateUserDtoResponse.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .birthDay(LocalDate.now().minusYears(20))
                .build();
    }

    private UserSearchDtoResponse createUserSearchDtoResponse() {
        return UserSearchDtoResponse.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .birthDay(LocalDate.now().minusYears(20))
                .gender(GenderType.MALE)
                .build();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserById_ValidId_ReturnsOk() throws Exception {
        Long userId = 1L;
        given(userService.findById(userId)).willReturn(createUserDto());

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserByEmail_ValidEmail_ReturnsOk() throws Exception {
        String email = "test@example.com";
        given(userService.findByEmail(email)).willReturn(createUserDto());

        mockMvc.perform(get("/users/email/{email}", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserInfo_ValidEmail_ReturnsOk() throws Exception {
        String email = "test@example.com";
        given(userService.getUserInfo(email)).willReturn(createUserInfoDto());

        mockMvc.perform(get("/users/info/{email}", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.wall.messages[0].content").value("Test message"));
    }

    @Test
    void createUser_ValidRequest_ReturnsCreated() throws Exception {
        CreateUserDtoRequest request = CreateUserDtoRequest.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .birthDay(LocalDate.now().minusYears(20))
                .gender(GenderType.MALE)
                .build();

        given(userService.create(any(CreateUserDtoRequest.class))).willReturn(createUserDtoResponse());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void createUser_InvalidRequest_ReturnsBadRequest() throws Exception {
        CreateUserDtoRequest invalidRequest = CreateUserDtoRequest.builder()
                .email("invalid-email")
                .firstName("")
                .lastName("")
                .birthDay(LocalDate.now().plusDays(1))
                .gender(null)
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void updateUser_ValidRequest_ReturnsOk() throws Exception {
        UpdateUserDtoRequest request = UpdateUserDtoRequest.builder()
                .firstName("Updated")
                .lastName("Name")
                .birthDay(LocalDate.now().minusYears(21))
                .gender(GenderType.FEMALE)
                .build();

        given(userService.update(any(UpdateUserDtoRequest.class))).willReturn(createUserSearchDtoResponse());

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateRoleUser_ValidRequest_ReturnsOk() throws Exception {
        UpdateRoleUserDtoRequest request = UpdateRoleUserDtoRequest.builder()
                .email("test@example.com")
                .role(RoleType.ROLE_ADMIN)
                .build();

        UpdateRoleUserDtoResponse response = UpdateRoleUserDtoResponse.builder()
                .email("test@example.com")
                .role(RoleType.ROLE_ADMIN)
                .build();

        given(userService.updateRole(any(UpdateRoleUserDtoRequest.class))).willReturn(response);

        mockMvc.perform(patch("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void searchUsers_WithFilters_ReturnsOk() throws Exception {
        List<UserSearchDtoResponse> users = Collections.singletonList(createUserSearchDtoResponse());
        given(userService.searchUsers(any())).willReturn(users);

        mockMvc.perform(get("/users/search")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("age", "20")
                        .param("gender", "MALE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void searchUsers_NoFilters_ReturnsOk() throws Exception {
        List<UserSearchDtoResponse> users = Collections.singletonList(createUserSearchDtoResponse());
        given(userService.searchUsers(any())).willReturn(users);

        mockMvc.perform(get("/users/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserById_InvalidId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/users/{id}", -1))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserByEmail_InvalidEmail_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/users/email/{email}", "invalid-email"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void updateUser_InvalidRequest_ReturnsBadRequest() throws Exception {
        UpdateUserDtoRequest invalidRequest = UpdateUserDtoRequest.builder()
                .firstName("")
                .lastName(null)
                .birthDay(LocalDate.now().plusDays(5))
                .gender(null)
                .build();

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateRoleUser_InvalidRequest_ReturnsBadRequest() throws Exception {
        UpdateRoleUserDtoRequest invalidRequest = UpdateRoleUserDtoRequest.builder()
                .email("not-an-email")
                .role(null)
                .build();

        mockMvc.perform(patch("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

}