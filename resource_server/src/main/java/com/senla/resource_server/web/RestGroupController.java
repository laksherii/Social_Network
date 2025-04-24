package com.senla.resource_server.web;

import com.senla.resource_server.service.dto.groupChat.CreateGroupChatRequestDto;
import com.senla.resource_server.service.dto.groupChat.CreateGroupChatResponseDto;
import com.senla.resource_server.service.impl.GroupChatServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/group-chat")
public class RestGroupController {

    private final GroupChatServiceImpl groupService;

    @PostMapping
    private CreateGroupChatResponseDto create(@RequestBody CreateGroupChatRequestDto groupDto) {
        return groupService.create(groupDto);
    }
}
