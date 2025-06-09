package com.senla.resource_server.web;

import com.senla.resource_server.service.dto.community.CommunityDto;
import com.senla.resource_server.service.dto.community.CreateCommunityRequestDto;
import com.senla.resource_server.service.dto.community.CreateCommunityResponseDto;
import com.senla.resource_server.service.dto.community.JoinCommunityRequestDto;
import com.senla.resource_server.service.dto.community.JoinCommunityResponseDto;
import com.senla.resource_server.service.interfaces.CommunityService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/communities")
public class RestCommunityController {

    private final CommunityService communityService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateCommunityResponseDto createCommunity(@Valid @RequestBody CreateCommunityRequestDto createDto) {
        CreateCommunityResponseDto response = communityService.createCommunity(createDto);
        log.info("Community created with name: {}", response.getCommunityName());
        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public JoinCommunityResponseDto joinCommunity(@Valid @RequestBody JoinCommunityRequestDto joinDto) {
        JoinCommunityResponseDto response = communityService.joinCommunity(joinDto);
        log.info("User joined community with ID: {}", joinDto.getCommunityId());
        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public JoinCommunityResponseDto getCommunity(@PathVariable @Positive Long id) {
        JoinCommunityResponseDto response = communityService.findCommunity(id);
        log.info("Found community with ID: {}", id);
        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommunityDto> getAllCommunities(
            @RequestParam @Positive Integer page,
            @RequestParam @Positive Integer size) {
        List<CommunityDto> communities = communityService.getAllCommunities(page, size);
        log.info("Found {} communities", communities.size());
        return communities;
    }

}