package com.senla.auth_server.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senla.auth_server.data.entity.User;
import com.senla.auth_server.service.dto.UserDtoRequest;
import com.senla.auth_server.service.dto.UserDtoResponse;
import com.senla.auth_server.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestUserController.class)
@Import(TestConfig.class)
class RestUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserServiceImpl userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDtoRequest buildValidUserDtoRequest() {
        return UserDtoRequest.builder()
                .email("john.doe@example.com")
                .password("securePass")
                .firstName("John")
                .lastName("Doe")
                .birthDay(LocalDate.of(1990, 1, 1))
                .gender(User.GenderType.MALE)
                .build();
    }

    private UserDtoResponse buildUserDtoResponseFrom(UserDtoRequest request) {
        return UserDtoResponse.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .birthDay(request.getBirthDay())
                .gender(request.getGender())
                .build();
    }

    @Test
    void createUser_WithValidRequest_ReturnsCreatedResponse() throws Exception {
        UserDtoRequest request = buildValidUserDtoRequest();
        UserDtoResponse response = buildUserDtoResponseFrom(request);

        Mockito.when(userService.create(any(UserDtoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(request.getEmail()))
                .andExpect(jsonPath("$.firstName").value(request.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(request.getLastName()));
    }

    @Test
    void createUser_WithInvalidEmailAndPassword_ReturnsBadRequest() throws Exception {
        UserDtoRequest request = buildValidUserDtoRequest();
        request.setEmail("");
        request.setPassword("123");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

