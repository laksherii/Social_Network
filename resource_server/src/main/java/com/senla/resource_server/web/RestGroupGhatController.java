package com.senla.resource_server.web;

import com.senla.resource_server.service.dto.groupChat.CreateGroupChatRequestDto;
import com.senla.resource_server.service.dto.groupChat.CreateGroupChatResponseDto;
import com.senla.resource_server.service.impl.GroupChatServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/group-chat")
public class RestGroupGhatController {

    private final GroupChatServiceImpl groupService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private CreateGroupChatResponseDto create(@Valid @RequestBody CreateGroupChatRequestDto groupDto) {
        log.info("User is creating a new group chat with name '{}'",
                groupDto.getName());
        CreateGroupChatResponseDto response = groupService.create(groupDto);
        log.info("User is created a new group chat with name '{}'",
                groupDto.getName());
        return response;
    }
}
