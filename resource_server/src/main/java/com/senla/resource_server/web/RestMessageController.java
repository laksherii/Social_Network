package com.senla.resource_server.web;

import com.senla.resource_server.service.interfaces.PublicMessageService;
import com.senla.resource_server.service.dto.community.SendCommunityMessageRequestDto;
import com.senla.resource_server.service.dto.community.SendCommunityMessageResponseDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageRequestDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageResponseDto;
import com.senla.resource_server.service.dto.message.PrivateMessageRequestDto;
import com.senla.resource_server.service.dto.message.PrivateMessageResponseDto;
import com.senla.resource_server.service.dto.wall.WallRequestDto;
import com.senla.resource_server.service.dto.wall.WallResponseDto;
import com.senla.resource_server.service.interfaces.PrivateMessageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
@RequestMapping("/messages")
public class RestMessageController {

    private final PrivateMessageService privateMessageService;
    private final PublicMessageService publicMessageService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @PostMapping("/communities")
    @ResponseStatus(HttpStatus.OK)
    public SendCommunityMessageResponseDto sendCommunityMessage(@Valid @RequestBody SendCommunityMessageRequestDto requestDto) {
        SendCommunityMessageResponseDto response = publicMessageService.sendCommunityMessage(requestDto);
        log.info("Successfully sent community message: {}", response);
        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @PostMapping("/group-chats")
    @ResponseStatus(HttpStatus.OK)
    public GroupChatMessageResponseDto sendGroupChatMessage(@Valid @RequestBody GroupChatMessageRequestDto requestDto) {
        GroupChatMessageResponseDto response = publicMessageService.sendGroupChatMessage(requestDto);
        log.info("Successfully sent group chat message: {}", response);
        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @PostMapping("/walls")
    @ResponseStatus(HttpStatus.OK)
    public WallResponseDto sendWallMessage(@Valid @RequestBody WallRequestDto requestDto) {
        WallResponseDto response = publicMessageService.sendWallMessage(requestDto);
        log.info("Successfully sent wall message: {}", response);
        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @GetMapping("/private")
    @ResponseStatus(HttpStatus.OK)
    public List<PrivateMessageResponseDto> getMessagesByRecipientEmail(@RequestParam @Email String email) {
        List<PrivateMessageResponseDto> messages = privateMessageService.getMessagesByRecipientEmail(email);
        log.info("Successfully fetched {} private messages for recipient email: {}", messages.size(), email);
        return messages;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @PostMapping("/private")
    @ResponseStatus(HttpStatus.OK)
    public PrivateMessageResponseDto sendPrivateMessage(@Valid @RequestBody PrivateMessageRequestDto privateMessageRequestDto) {
        PrivateMessageResponseDto privateMessageResponseDto = privateMessageService.sendPrivateMessage(privateMessageRequestDto);
        log.info("Successfully sent private message from user to recipient with email: {}", privateMessageRequestDto.getRecipientEmail());
        return privateMessageResponseDto;
    }
}

