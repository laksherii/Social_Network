package com.senla.resource_server.integration;

import com.senla.resource_server.data.dao.impl.GroupChatDaoImpl;
import com.senla.resource_server.data.dao.impl.UserDaoImpl;
import com.senla.resource_server.data.entity.GroupChat;
import com.senla.resource_server.data.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = {GroupChatDaoImpl.class, TestDataConfig.class, UserDaoImpl.class})
class GroupChatDaoImplTest extends BaseIntegrationTest {

    @Autowired
    private GroupChatDaoImpl groupChatDao;

    @Autowired
    private UserDaoImpl userDao;

    @Test
    @Transactional
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:groupChatDao/insert_group_chat.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void save_ShouldPersistGroupChatWithUsers() {
        // given
        User user1 = userDao.findById(1L).orElseThrow();
        User user2 = userDao.findById(2L).orElseThrow();

        GroupChat groupChat = GroupChat.builder()
                .name("New Chat")
                .users(Set.of(user1, user2))
                .build();

        // when
        GroupChat saved = groupChatDao.save(groupChat);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo(groupChat.getName());
    }

    @Test
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:groupChatDao/insert_group_chat.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void findById_ShouldReturnGroupChat() {
        // when
        GroupChat groupChat = groupChatDao.findById(10L).orElseThrow();
        // then
        assertThat(groupChat).isNotNull();
        assertThat(groupChat.getId()).isEqualTo(10L);
    }
}

