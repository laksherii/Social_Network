package com.senla.resource_server.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senla.resource_server.exception.EntityNotFoundException;
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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestCommunityController.class)
@Import(TestConfig.class)
class RestCommunityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommunityService communityService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = {"USER"})
    void createCommunity_ValidRequest_ReturnsCreated() throws Exception {
        CreateCommunityRequestDto request = new CreateCommunityRequestDto("Devs", "For developers");
        CreateCommunityResponseDto response = new CreateCommunityResponseDto("Devs");

        given(communityService.createCommunity(any())).willReturn(response);

        mockMvc.perform(post("/community")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.communityName").value("Devs"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void createCommunity_InvalidRequest_ReturnsBadRequest() throws Exception {
        CreateCommunityRequestDto invalidRequest = new CreateCommunityRequestDto("", "");

        mockMvc.perform(post("/community")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void joinCommunity_ValidRequest_ReturnsOk() throws Exception {
        JoinCommunityRequestDto request = new JoinCommunityRequestDto(1L);
        JoinCommunityResponseDto response = new JoinCommunityResponseDto(
                "Devs",
                "For developers",
                Collections.emptyList()
        );

        given(communityService.joinCommunity(any())).willReturn(response);

        mockMvc.perform(put("/community")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.communityName").value("Devs"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void joinCommunity_InvalidCommunityId_ReturnsNotFound() throws Exception {
        JoinCommunityRequestDto request = new JoinCommunityRequestDto(999L);

        given(communityService.joinCommunity(any()))
                .willThrow(new EntityNotFoundException("Community not found"));

        mockMvc.perform(put("/community")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void getCommunity_ValidId_ReturnsOk() throws Exception {
        JoinCommunityResponseDto response = new JoinCommunityResponseDto(
                "Devs",
                "For developers",
                List.of(new CommunityMessageDto("Hello"))
        );

        given(communityService.findCommunity(1L)).willReturn(response);

        mockMvc.perform(get("/community/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.communityName").value("Devs"))
                .andExpect(jsonPath("$.messages", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void getCommunity_InvalidId_ReturnsNotFound() throws Exception {
        given(communityService.findCommunity(999L))
                .willThrow(new EntityNotFoundException("Not community found"));

        mockMvc.perform(get("/community/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void getAllCommunities_ValidPagination_ReturnsList() throws Exception {
        List<CommunityDto> communities = List.of(
                new CommunityDto("Devs", "For developers"),
                new CommunityDto("Designers", "UI/UX")
        );

        given(communityService.getAllCommunities(1, 2)).willReturn(communities);

        mockMvc.perform(get("/community")
                        .param("page", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Devs"))
                .andExpect(jsonPath("$[1].name").value("Designers"));
    }
}

