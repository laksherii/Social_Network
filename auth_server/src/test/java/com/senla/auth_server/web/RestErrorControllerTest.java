package com.senla.auth_server.web;

import com.senla.auth_server.exception.EmailAlreadyExistsException;
import com.senla.auth_server.service.dto.ErrorDto.ErrorStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RestErrorController.class)
@Import({TestConfig.class, RestErrorControllerTest.TestController.class})
class RestErrorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHandleMethodArgumentNotValidException() throws Exception {
        mockMvc.perform(post("/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("username: must not be blank"));
    }

    @Test
    void testHandleBadCredentialsException() throws Exception {
        mockMvc.perform(post("/test/bad-credentials"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorStatus").value(ErrorStatus.CLIENT_ERROR.name()))
                .andExpect(jsonPath("$.errorMessage").value("Invalid credentials"));
    }

    @Test
    void testHandleUsernameNotFoundException() throws Exception {
        mockMvc.perform(post("/test/username-not-found"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorStatus").value(ErrorStatus.CLIENT_ERROR.name()))
                .andExpect(jsonPath("$.errorMessage").value("User not found"));
    }

    @Test
    void testHandleEmailAlreadyExistsException() throws Exception {
        mockMvc.perform(post("/test/email-exists"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorStatus").value(ErrorStatus.CLIENT_ERROR.name()))
                .andExpect(jsonPath("$.errorMessage").value("Email already exists"));
    }

    @Test
    void testHandleRestClientException() throws Exception {
        mockMvc.perform(post("/test/rest-client"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorStatus").value(ErrorStatus.CLIENT_ERROR.name()))
                .andExpect(jsonPath("$.errorMessage").value("External service error"));
    }

    @Test
    void testHandleGenericException() throws Exception {
        mockMvc.perform(post("/test/generic"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorStatus").value(ErrorStatus.SERVER_ERROR.name()))
                .andExpect(jsonPath("$.errorMessage").value("An unexpected error occurred."));
    }

    @RestController
    @RequestMapping("/test")
    static class TestController {

        @PostMapping("/validation")
        public void triggerValidation(@RequestBody @Valid TestDto dto) {
        }

        @PostMapping("/bad-credentials")
        public void triggerBadCredentials() {
            throw new BadCredentialsException("Invalid credentials");
        }

        @PostMapping("/username-not-found")
        public void triggerUsernameNotFound() {
            throw new UsernameNotFoundException("User not found");
        }

        @PostMapping("/email-exists")
        public void triggerEmailAlreadyExists() {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        @PostMapping("/rest-client")
        public void triggerRestClient() {
            throw new org.springframework.web.client.RestClientException("External service error");
        }

        @PostMapping("/generic")
        public void triggerGenericException() {
            throw new RuntimeException("Some unexpected error");
        }

        @Data
        static class TestDto {
            @NotBlank
            private String username;
        }
    }
}
