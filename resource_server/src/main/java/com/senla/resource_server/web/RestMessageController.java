package com.senla.resource_server.web;

import com.senla.resource_server.service.dto.message.GetGroupChatMessageDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageRequestDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageResponseDto;
import com.senla.resource_server.service.dto.message.PrivateMessageRequestDto;
import com.senla.resource_server.service.dto.message.PrivateMessageResponseDto;
import com.senla.resource_server.service.dto.message.SendCommunityMessageRequestDto;
import com.senla.resource_server.service.dto.message.SendCommunityMessageResponseDto;
import com.senla.resource_server.service.impl.CommunityMessageServiceImpl;
import com.senla.resource_server.service.impl.GroupChatMessageServiceImpl;
import com.senla.resource_server.service.impl.PrivateMessageServiceImpl;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/message")
public class RestMessageController {

    private final PrivateMessageServiceImpl privateMessageService;
    private final GroupChatMessageServiceImpl groupChatMessageServiceImpl;
    private final CommunityMessageServiceImpl communityMessageServiceImpl;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @GetMapping("/private")
    @ResponseStatus(HttpStatus.OK)
    public List<PrivateMessageResponseDto> getMessagesByRecipientEmail(@Valid @RequestParam String email) {
        log.info("Fetching private messages for recipient email: {}", email);
        List<PrivateMessageResponseDto> messages = privateMessageService.getMessagesByRecipientEmail(email);
        log.info("Successfully fetched {} private messages for recipient email: {}", messages.size(), email);
        return messages;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @PostMapping("/private")
    @ResponseStatus(HttpStatus.OK)
    public PrivateMessageResponseDto sendPrivateMessage(@Valid @RequestBody PrivateMessageRequestDto privateMessageRequestDto) {
        log.info("Sending private message to recipient with email: {}",
                privateMessageRequestDto.getRecipientEmail());
        PrivateMessageResponseDto privateMessageResponseDto = privateMessageService.sendPrivateMessage(privateMessageRequestDto);
        log.info("Successfully sent private message from user to recipient with email: {}",
                privateMessageRequestDto.getRecipientEmail());
        return privateMessageResponseDto;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @GetMapping("/group-chat/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<GetGroupChatMessageDto> getGroupChatMessages(@PathVariable Long id) {
        log.info("Fetching group chat messages for group chat with ID: {}", id);
        List<GetGroupChatMessageDto> groupChatMessages = groupChatMessageServiceImpl.getGroupChatMessages(id);
        log.info("Successfully fetched group chat messages for group chat with ID: {}", id);
        return groupChatMessages;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @PostMapping("/group-chat")
    @ResponseStatus(HttpStatus.OK)
    public GroupChatMessageResponseDto sendGroupChatMessage(@Valid @RequestBody GroupChatMessageRequestDto groupChatMessageRequest) {
        log.info("Sending group chat message from user to group chat ID: {}",
                groupChatMessageRequest.getGroupId());
        GroupChatMessageResponseDto response = groupChatMessageServiceImpl.sendGroupChatMessage(groupChatMessageRequest);
        log.info("Successfully sent group chat message from user to group chat ID: {}",
                groupChatMessageRequest.getGroupId());
        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @PostMapping("/community")
    @ResponseStatus(HttpStatus.OK)
    public SendCommunityMessageResponseDto sendCommunityMessage(@Valid @RequestBody SendCommunityMessageRequestDto communityMessageDto) {
        log.info("Sending community message from user to community ID: {}",
                communityMessageDto.getCommunityId());
        SendCommunityMessageResponseDto response = communityMessageServiceImpl.sendCommunityMessage(communityMessageDto);
        log.info("Successfully sent community message from user to community ID: {}",
                communityMessageDto.getCommunityId());
        return response;
    }
}
