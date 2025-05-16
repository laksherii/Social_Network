package com.senla.resource_server.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senla.resource_server.config.JwtAuthenticationFilter;
import com.senla.resource_server.config.JwtTokenProvider;
import com.senla.resource_server.service.dto.community.CommunityDto;
import com.senla.resource_server.service.dto.community.CommunityMessageDto;
import com.senla.resource_server.service.dto.community.CreateCommunityRequestDto;
import com.senla.resource_server.service.dto.community.CreateCommunityResponseDto;
import com.senla.resource_server.service.dto.community.JoinCommunityRequestDto;
import com.senla.resource_server.service.dto.community.JoinCommunityResponseDto;
import com.senla.resource_server.service.interfaces.CommunityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestCommunityController.class)
@WithMockUser(username = "test@example.com", roles = "ADMIN")
class RestCommunityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommunityService communityService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCommunity_WhenValid_ShouldReturnCreated() throws Exception {
        // Given
        CreateCommunityRequestDto request = new CreateCommunityRequestDto("Test Community", "Test");
        CreateCommunityResponseDto response = new CreateCommunityResponseDto("Test Community");
        given(communityService.createCommunity(request)).willReturn(response);

        // When / Then
        mockMvc.perform(post("/community")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.communityName").value("Test Community"));
    }

    @Test
    void joinCommunity_WhenValid_ShouldReturnOk() throws Exception {
        // Given
        JoinCommunityRequestDto request = new JoinCommunityRequestDto(1L);
        CommunityMessageDto message = new CommunityMessageDto();
        message.setContent("Welcome!");
        JoinCommunityResponseDto response = new JoinCommunityResponseDto("Test Community", "Test", List.of(message));
        given(communityService.joinCommunity(request)).willReturn(response);

        // When / Then
        mockMvc.perform(put("/community")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.communityName").value("Test Community"))
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages[0].message").value("Welcome!"));
    }

    @Test
    void getCommunity_WhenExists_ShouldReturnCommunity() throws Exception {
        // Given
        CommunityMessageDto message = new CommunityMessageDto();
        message.setContent("Test Message");
        JoinCommunityResponseDto response = new JoinCommunityResponseDto("Test Community", "Test", List.of(message));
        given(communityService.findCommunity(1L)).willReturn(response);

        // When / Then
        mockMvc.perform(get("/community/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.communityName").value("Test Community"))
                .andExpect(jsonPath("$.messages[0].message").value("Test Message"));
    }

    @Test
    void getAllCommunities_ShouldReturnList() throws Exception {
        // Given
        List<CommunityDto> communities = List.of(
                new CommunityDto("Community 1", "Test Community 1"),
                new CommunityDto("Community 2", "Test Community 2")
        );
        given(communityService.getAllCommunities(0, 10)).willReturn(communities);

        // When / Then
        mockMvc.perform(get("/community")
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Community 1"))
                .andExpect(jsonPath("$[1].name").value("Community 2"));
    }
}

