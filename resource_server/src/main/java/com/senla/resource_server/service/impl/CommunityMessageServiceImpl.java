package com.senla.resource_server.service.impl;

import com.senla.resource_server.config.SecurityConfig;
import com.senla.resource_server.data.dao.impl.CommunityDaoImpl;
import com.senla.resource_server.data.dao.impl.CommunityMessageDaoImpl;
import com.senla.resource_server.data.dao.impl.UserDaoImpl;
import com.senla.resource_server.data.entity.Community;
import com.senla.resource_server.data.entity.CommunityMessage;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.data.mapper.MessageMapper;
import com.senla.resource_server.exception.EntityNotFoundException;
import com.senla.resource_server.exception.IllegalStateException;
import com.senla.resource_server.service.dto.message.CreateCommunityMessageRequestDto;
import com.senla.resource_server.service.dto.message.CreateCommunityMessageResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommunityMessageServiceImpl {

    private final CommunityDaoImpl communityDao;
    private final UserDaoImpl userDaoImpl;
    private final CommunityMessageDaoImpl communityMessageDaoImpl;
    private final MessageMapper messageMapper;

    public CreateCommunityMessageResponseDto sendCommunityMessage(CreateCommunityMessageRequestDto createCommunityMessageDto) {

        Long communityId = createCommunityMessageDto.getCommunityId();
        Community community = communityDao.findById(communityId);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userDaoImpl.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Not found user"));

        if (!user.getEmail().equals(community.getAdmin().getEmail()) && community.getAdmin().getEmail() != null) {
            throw new IllegalStateException("Добавлять записи на стену сообщества может только администратор сообщества");
        }

        String message = createCommunityMessageDto.getMessage();
        CommunityMessage communityMessage = new CommunityMessage();
        communityMessage.setCommunity(community);
        communityMessage.setMessage(message);
        communityMessage.setSender(user);

        List<CommunityMessage> messages = community.getMessages();
        messages.add(communityMessage);

        CommunityMessage save = communityMessageDaoImpl.save(communityMessage);
        return messageMapper.toCreateCommunityMessageResponseDto(save);
    }
}
