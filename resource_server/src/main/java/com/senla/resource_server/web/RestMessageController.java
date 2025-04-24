package com.senla.resource_server.web;

import com.senla.resource_server.service.dto.message.CreateCommunityMessageRequestDto;
import com.senla.resource_server.service.dto.message.CreateCommunityMessageResponseDto;
import com.senla.resource_server.service.dto.message.GroupChatMessageRequest;
import com.senla.resource_server.service.dto.message.GroupChatMessageResponse;
import com.senla.resource_server.service.dto.message.PrivateMessageRequestDto;
import com.senla.resource_server.service.dto.message.PrivateMessageResponseDto;
import com.senla.resource_server.service.impl.CommunityMessageServiceImpl;
import com.senla.resource_server.service.impl.GroupChatMessageServiceImpl;
import com.senla.resource_server.service.impl.PrivateMessageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class RestMessageController {

    private final PrivateMessageServiceImpl privateMessageService;
    private final GroupChatMessageServiceImpl groupChatMessageServiceImpl;
    private final CommunityMessageServiceImpl communityMessageServiceImpl;

    @PostMapping("/private")
    private PrivateMessageResponseDto sendPrivateMessage(@RequestBody PrivateMessageRequestDto privateMessageRequestDto) {
        return privateMessageService.sendPrivateMessage(privateMessageRequestDto);
    }

    @PostMapping("/group-chat")
    private GroupChatMessageResponse sendGroupChatMessage(@RequestBody GroupChatMessageRequest groupChatMessageRequest) {
        return groupChatMessageServiceImpl.sendGroupChatMessage(groupChatMessageRequest);
    }

    @PostMapping("/community-message")
    private CreateCommunityMessageResponseDto sendCommunityMessage(@RequestBody CreateCommunityMessageRequestDto communityMessageDto) {
        return communityMessageServiceImpl.sendCommunityMessage(communityMessageDto);
    }
}
