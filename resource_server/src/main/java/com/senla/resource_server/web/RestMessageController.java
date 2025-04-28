package com.senla.resource_server.web;

import com.senla.resource_server.service.dto.message.CreateCommunityMessageRequestDto;
import com.senla.resource_server.service.dto.message.CreateCommunityMessageResponseDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageRequestDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageResponseDto;
import com.senla.resource_server.service.dto.message.PrivateMessageRequestDto;
import com.senla.resource_server.service.dto.message.PrivateMessageResponseDto;
import com.senla.resource_server.service.impl.CommunityMessageServiceImpl;
import com.senla.resource_server.service.impl.GroupChatMessageServiceImpl;
import com.senla.resource_server.service.impl.PrivateMessageServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class RestMessageController {

    private final PrivateMessageServiceImpl privateMessageService;
    private final GroupChatMessageServiceImpl groupChatMessageServiceImpl;
    private final CommunityMessageServiceImpl communityMessageServiceImpl;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @GetMapping("/private")
    @ResponseStatus(HttpStatus.OK)
    private List<PrivateMessageResponseDto> getMessagesByRecipientEmail(@RequestParam String email) {
        log.info("Fetching private messages for recipient email: {}", email);
        List<PrivateMessageResponseDto> messages = privateMessageService.getMessagesByRecipientEmail(email);
        log.info("Successfully fetched {} private messages for recipient email: {}", messages.size(), email);
        return messages;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @PostMapping("/private")
    @ResponseStatus(HttpStatus.OK)
    private PrivateMessageResponseDto sendPrivateMessage(@RequestBody PrivateMessageRequestDto privateMessageRequestDto) {
        log.info("Sending private message to recipient with email: {}",
                privateMessageRequestDto.getRecipient());
        PrivateMessageResponseDto privateMessageResponseDto = privateMessageService.sendPrivateMessage(privateMessageRequestDto);
        log.info("Successfully sent private message from user to recipient with email: {}",
                privateMessageRequestDto.getRecipient());
        return privateMessageResponseDto;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    private List<GroupChatMessageResponseDto> getGroupChatMessages(@PathVariable Long id) {
        log.info("Fetching group chat messages for group chat with ID: {}", id);
        List<GroupChatMessageResponseDto> groupChatMessages = groupChatMessageServiceImpl.getGroupChatMessages(id);
        log.info("Successfully fetched {} group chat messages for group chat with ID: {}", groupChatMessages.size(), id);
        return groupChatMessages;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @PostMapping("/group-chat")
    @ResponseStatus(HttpStatus.OK)
    private GroupChatMessageResponseDto sendGroupChatMessage(@RequestBody GroupChatMessageRequestDto groupChatMessageRequest) {
        log.info("Sending group chat message from user to group chat ID: {}",
                groupChatMessageRequest.getGroupId());
        GroupChatMessageResponseDto response = groupChatMessageServiceImpl.sendGroupChatMessage(groupChatMessageRequest);
        log.info("Successfully sent group chat message from user to group chat ID: {}",
                groupChatMessageRequest.getGroupId());
        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @PostMapping("/community-message")
    @ResponseStatus(HttpStatus.OK)
    private CreateCommunityMessageResponseDto sendCommunityMessage(@RequestBody CreateCommunityMessageRequestDto communityMessageDto) {
        log.info("Sending community message from user to community ID: {}",
                communityMessageDto.getCommunityId());
        CreateCommunityMessageResponseDto response = communityMessageServiceImpl.sendCommunityMessage(communityMessageDto);
        log.info("Successfully sent community message from user to community ID: {}",
                communityMessageDto.getCommunityId());
        return response;
    }
}
