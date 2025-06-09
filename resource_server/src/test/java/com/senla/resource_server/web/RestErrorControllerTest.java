package com.senla.resource_server.web;

import com.senla.resource_server.exception.BadRequestParamException;
import com.senla.resource_server.exception.EntityExistException;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.exception.IllegalArgumentException;
import com.senla.resource_server.exception.IllegalStateException;
import com.senla.resource_server.exception.UserNotAdminInGroupException;
import com.senla.resource_server.exception.UserNotInGroupChatException;
import com.senla.resource_server.exception.UserNotYourFriendException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RestErrorController.class)
@Import({TestConfig.class, RestErrorControllerTest.TestController.class})
@WithMockUser(username = "test@example.com", roles = "ADMIN")
class RestErrorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void handleAuthorizationDeniedException_ReturnsForbiddenStatus() throws Exception {
        mockMvc.perform(get("/test/auth-denied")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorStatus").value("CLIENT_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("Access denied"));
    }

    @Test
    void handleUserNotAdminInGroupException_ReturnsConflictStatus() throws Exception {
        mockMvc.perform(get("/test/not-admin")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorStatus").value("CLIENT_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("Not admin"));
    }

    @Test
    void handleEntityNotFoundException_ReturnsNotFoundStatus() throws Exception {
        mockMvc.perform(get("/test/not-found")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorStatus").value("CLIENT_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("Not found"));
    }

    @Test
    void handleBadRequestParamException_ReturnsBadRequestStatus() throws Exception {
        mockMvc.perform(get("/test/bad-param")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorStatus").value("CLIENT_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("Bad param"));
    }

    @Test
    void handleIllegalStateException_ReturnsConflictStatus() throws Exception {
        mockMvc.perform(get("/test/illegal-state")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorStatus").value("CLIENT_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("Illegal state"));
    }

    @Test
    void handleIllegalArgumentException_ReturnsBadRequestStatus() throws Exception {
        mockMvc.perform(get("/test/illegal-arg")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorStatus").value("CLIENT_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("Illegal argument"));
    }

    @Test
    void handleEntityExistException_ReturnsConflictStatus() throws Exception {
        mockMvc.perform(get("/test/entity-exists")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorStatus").value("CLIENT_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("Entity exists"));
    }

    @Test
    void handleUserNotInGroupChatException_ReturnsConflictStatus() throws Exception {
        mockMvc.perform(get("/test/not-in-group")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorStatus").value("CLIENT_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("Not in group"));
    }

    @Test
    void handleBadCredentialsException_ReturnsBadRequestStatus() throws Exception {
        mockMvc.perform(get("/test/bad-credentials")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorStatus").value("CLIENT_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("Bad credentials"));
    }

    @Test
    void handleMethodArgumentNotValidException_ReturnsBadRequest() throws Exception {
        String json = "{\"fieldName\":\"\"}";
        mockMvc.perform(post("/test/method-not-valid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void handleConstraintViolationException_ReturnsBadRequestStatus() throws Exception {
        mockMvc.perform(get("/test/violation")
                        .content(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("field1: must not be empty"));
    }

    @Test
    void handleUserNotYourFriendException_ReturnsForbiddenStatus() throws Exception {
        mockMvc.perform(get("/test/user-not-friend")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorStatus").value("CLIENT_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("User not your friend"));
    }

    @Test
    void handleGenericException_ReturnsInternalServerErrorStatus() throws Exception {
        mockMvc.perform(get("/test/generic-error")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorStatus").value("SERVER_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("Oops! Something went wrong"));
    }

    @RestController
    @RequestMapping("/test")
    static class TestController {

        @GetMapping("/auth-denied")
        public void throwAuthDenied() {
            throw new AuthorizationDeniedException("Access denied");
        }

        @GetMapping("/not-admin")
        public void throwNotAdmin() {
            throw new UserNotAdminInGroupException("Not admin");
        }

        @GetMapping("/not-found")
        public void throwNotFound() {
            throw new EntityNotFoundException("Not found");
        }

        @GetMapping("/bad-param")
        public void throwBadParam() {
            throw new BadRequestParamException("Bad param");
        }

        @GetMapping("/illegal-state")
        public void throwIllegalState() {
            throw new IllegalStateException("Illegal state");
        }

        @GetMapping("/illegal-arg")
        public void throwIllegalArg() {
            throw new IllegalArgumentException("Illegal argument");
        }

        @GetMapping("/entity-exists")
        public void throwEntityExists() {
            throw new EntityExistException("Entity exists");
        }

        @GetMapping("/not-in-group")
        public void throwNotInGroup() {
            throw new UserNotInGroupChatException("Not in group");
        }

        @GetMapping("/bad-credentials")
        public void throwBadCredential() {
            throw new BadCredentialsException("Bad credentials");
        }

        @GetMapping("/user-not-friend")
        public void throwUserNotYourFriend() {
            throw new UserNotYourFriendException("User not your friend");
        }

        @PostMapping("/method-not-valid")
        public void throwMethodArgumentNotValid(@Valid @RequestBody TestDto dto) {
        }

        @GetMapping("/violation")
        public void throwConstraintViolation() {
            ConstraintViolation<?> violation = Mockito.mock(ConstraintViolation.class);
            Path path = Mockito.mock(Path.class);

            when(violation.getMessage()).thenReturn("must not be empty");
            when(violation.getPropertyPath()).thenReturn(path);
            when(path.toString()).thenReturn("field1");

            throw new ConstraintViolationException(Set.of(violation));
        }

        @GetMapping("/generic-error")
        public void throwGenericError() {
            throw new RuntimeException("Unexpected error");
        }
    }

    @Setter
    @Getter
    public static class TestDto {
        @NotEmpty(message = "The field must not be empty")
        private String fieldName;

    }
}