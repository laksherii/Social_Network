package com.senla.resource_server.service.mapper;

import com.senla.resource_server.data.entity.FriendRequest;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.service.dto.friendRequest.AnswerFriendResponseDto;
import com.senla.resource_server.service.dto.friendRequest.SendFriendResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.senla.resource_server.data.entity.FriendRequest.FriendRequestStatus.ACCEPTED;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class FriendRequestMapperImplTest {

    private final FriendRequestMapper friendRequestMapper = Mappers.getMapper(FriendRequestMapper.class);

    @Test
    void toSendFriendResponseDto_shouldReturnNull_whenInputIsNull() {
        // given
        FriendRequest request = null;

        // when
        SendFriendResponseDto result = friendRequestMapper.toSendFriendResponseDto(request);

        // then
        assertThat(result).isNull();
    }

    @Test
    void toSendFriendResponseDto_shouldReturnDtoWithNullRecipient_whenRecipientIsNull() {
        // given
        FriendRequest request = FriendRequest.builder()
                .recipient(null)
                .status(ACCEPTED)
                .build();

        // when
        SendFriendResponseDto result = friendRequestMapper.toSendFriendResponseDto(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getRecipient()).isNull();
        assertThat(result.getStatus()).isEqualTo(ACCEPTED);
    }

    @Test
    void toSendFriendResponseDto_shouldMapFieldsCorrectly_whenRecipientIsPresent() {
        // given
        User recipient = User.builder().email("test@example.com").build();
        FriendRequest request = FriendRequest.builder()
                .recipient(recipient)
                .status(ACCEPTED)
                .build();

        // when
        SendFriendResponseDto result = friendRequestMapper.toSendFriendResponseDto(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getRecipient()).isNotNull();
        assertThat(result.getRecipient().getEmail()).isEqualTo("test@example.com");
        assertThat(result.getStatus()).isEqualTo(ACCEPTED);
    }

    @Test
    void toAnswerFriendResponseDto_shouldReturnNull_whenInputIsNull() {
        // given
        FriendRequest request = null;

        // when
        AnswerFriendResponseDto result = friendRequestMapper.toAnswerFriendResponseDto(request);

        // then
        assertThat(result).isNull();
    }

    @Test
    void toAnswerFriendResponseDto_shouldReturnDtoWithNullSender_whenSenderIsNull() {
        // given
        FriendRequest request = FriendRequest.builder()
                .sender(null)
                .status(ACCEPTED)
                .build();

        // when
        AnswerFriendResponseDto result = friendRequestMapper.toAnswerFriendResponseDto(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getSender()).isNull();
        assertThat(result.getStatus()).isEqualTo(ACCEPTED);
    }

    @Test
    void toAnswerFriendResponseDto_shouldMapFieldsCorrectly_whenSenderIsPresent() {
        // given
        User sender = User.builder().email("sender@example.com").build();
        FriendRequest request = FriendRequest.builder()
                .sender(sender)
                .status(ACCEPTED)
                .build();

        // when
        AnswerFriendResponseDto result = friendRequestMapper.toAnswerFriendResponseDto(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getSender()).isNotNull();
        assertThat(result.getSender().getEmail()).isEqualTo("sender@example.com");
        assertThat(result.getStatus()).isEqualTo(ACCEPTED);
    }
}
