package com.senla.resource_server.web;

import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.service.dto.groupChat.CreateGroupChatRequestDto;
import com.senla.resource_server.service.dto.groupChat.CreateGroupChatResponseDto;
import com.senla.resource_server.service.dto.groupChat.GroupChatDto;
import com.senla.resource_server.service.dto.message.GetGroupChatMessageDto;
import com.senla.resource_server.service.impl.GroupChatServiceImpl;
import com.senla.resource_server.service.mapper.GroupChatMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/group-chat")
public class RestGroupChatController {

    private final GroupChatServiceImpl groupService;
    private final GroupChatMapper groupChatMapper;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateGroupChatResponseDto create(@Valid @RequestBody CreateGroupChatRequestDto groupDto) {
        log.info("User is creating a new group chat with name '{}'",
                groupDto.getName());
        CreateGroupChatResponseDto response = groupService.create(groupDto);
        log.info("User is created a new group chat with name '{}'",
                groupDto.getName());
        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public GroupChatDto findCommunity(@PathVariable Long id) {
        log.info("User is attempting to find a group chat with ID: {}", id);
        GroupChatDto groupChat = groupService.findById(id);
        log.info("Successfully retrieved group chat with name: {}", groupChat.getName());
        return groupChat;
    }

}
