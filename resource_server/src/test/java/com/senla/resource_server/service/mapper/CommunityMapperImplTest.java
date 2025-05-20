package com.senla.resource_server.service.mapper;

import com.senla.resource_server.data.entity.Community;
import com.senla.resource_server.service.dto.community.CommunityDto;
import com.senla.resource_server.service.dto.community.CreateCommunityResponseDto;
import com.senla.resource_server.service.dto.community.JoinCommunityResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CommunityMapperImplTest {

    private final CommunityMapper communityMapper = Mappers.getMapper(CommunityMapper.class);

    @Test
    void toCreateCommunityResponseDto_shouldReturnNull_whenInputIsNull() {
        // given
        Community community = null;

        // when
        CreateCommunityResponseDto dto = communityMapper.toCreateCommunityResponseDto(community);

        // then
        assertThat(dto).isNull();
    }

    @Test
    void toCreateCommunityResponseDto_shouldMapCorrectly_whenCommunityIsNotNull() {
        // given
        Community community = Community.builder().name("TestCommunity").build();

        // when
        CreateCommunityResponseDto dto = communityMapper.toCreateCommunityResponseDto(community);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getCommunityName()).isEqualTo("TestCommunity");
    }

    @Test
    void toJoinCommunityResponseDto_shouldReturnNull_whenInputIsNull() {
        // given
        Community community = null;

        // when
        JoinCommunityResponseDto dto = communityMapper.toJoinCommunityResponseDto(community);

        // then
        assertThat(dto).isNull();
    }

    @Test
    void toJoinCommunityResponseDto_shouldReturnDtoWithNullMessages_whenCommunityHasNullMessages() {
        // given
        Community community = Community.builder()
                .name("Java")
                .description("Java description")
                .messages(null)
                .build();

        // when
        JoinCommunityResponseDto dto = communityMapper.toJoinCommunityResponseDto(community);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getCommunityName()).isEqualTo("Java");
        assertThat(dto.getDescription()).isEqualTo("Java description");
        assertThat(dto.getMessages()).isNull();
    }

    @Test
    void toCommunityDto_shouldReturnNull_whenInputIsNull() {
        // given
        Community community = null;

        // when
        CommunityDto dto = communityMapper.toCommunityDto(community);

        // then
        assertThat(dto).isNull();
    }

    @Test
    void toCommunityDto_shouldMapCorrectly_whenCommunityIsNotNull() {
        // given
        Community community = Community.builder()
                .name("Kotlin")
                .description("Kotlin group")
                .build();

        // when
        CommunityDto dto = communityMapper.toCommunityDto(community);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("Kotlin");
        assertThat(dto.getDescription()).isEqualTo("Kotlin group");
    }
}
