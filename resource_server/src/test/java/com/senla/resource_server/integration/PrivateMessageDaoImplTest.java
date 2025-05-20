package com.senla.resource_server.integration;

import com.senla.resource_server.data.dao.impl.PrivateMessageDaoImpl;
import com.senla.resource_server.data.dao.impl.UserDaoImpl;
import com.senla.resource_server.data.entity.PrivateMessage;
import com.senla.resource_server.data.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {PrivateMessageDaoImpl.class, TestDataConfig.class, UserDaoImpl.class})
public class PrivateMessageDaoImplTest extends BaseIntegrationTest {

    @Autowired
    private PrivateMessageDaoImpl dao;

    @Autowired
    private UserDaoImpl userDao;

    @Test
    @Transactional
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:privateMessageDao/insert_private_message.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void save_shouldPersistMessage() {

        User sender = userDao.findById(1L).orElseThrow();
        User recipient = userDao.findById(2L).orElseThrow();

        PrivateMessage message = PrivateMessage.builder()
                .content("Test message content")
                .sender(sender)
                .recipient(recipient)
                .createdAt(Instant.now())
                .build();

        PrivateMessage saved = dao.save(message);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getContent()).isEqualTo("Test message content");
        assertThat(saved.getSender().getId()).isEqualTo(1L);
        assertThat(saved.getRecipient().getId()).isEqualTo(2L);
    }

    @Test
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:privateMessageDao/insert_private_message.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void findBySenderAndRecipient_shouldReturnMessages() {
        User sender = userDao.findById(1L).orElseThrow();
        User recipient = userDao.findById(2L).orElseThrow();

        List<PrivateMessage> messages = dao.findBySenderAndRecipient(sender, recipient);

        assertThat(messages).isNotNull();
        assertThat(messages).hasSize(1);
    }

    @Test
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:privateMessageDao/insert_private_message.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void findBySenderAndRecipient_shouldReturnEmptyListWhenNoMessages() {
        User sender = userDao.findById(3L).orElseThrow();
        User recipient = userDao.findById(4L).orElseThrow();

        List<PrivateMessage> messages = dao.findBySenderAndRecipient(sender, recipient);

        assertThat(messages).isEmpty();
    }
}
