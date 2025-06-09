package com.senla.auth_server.integration;

import com.senla.auth_server.data.dao.UserDaoImpl;
import com.senla.auth_server.data.entity.User;
import com.senla.auth_server.data.entity.User.GenderType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {UserDaoImpl.class, TestDataConfig.class})
public class UserDaoImplTest extends BaseIntegrationTest {

    @Autowired
    private UserDaoImpl dao;

    @Test
    @Sql(value = "classpath:userDao/truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:userDao/insert_user.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:userDao/truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void findByEmail() {
        Optional<User> found = dao.findByEmail("john.doe@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
    }

    @Test
    @Sql(value = "classpath:userDao/truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:userDao/insert_user.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:userDao/truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void findByEmail_shouldReturnEmptyIfUserNotFound() {
        // when
        Optional<User> result = dao.findByEmail("notfound@example.com");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @Sql(value = "classpath:userDao/truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:userDao/truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    @Transactional
    public void save() {

        User testUser = User.builder()
                .email("jane.smith@example.com")
                .password("password")
                .firstName("Jane")
                .lastName("Smith")
                .birthDay(LocalDate.of(1992, 8, 20))
                .enabled(true)
                .gender(GenderType.FEMALE)
                .build();

        User saved = dao.save(testUser);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("jane.smith@example.com");
        assertThat(saved.getFirstName()).isEqualTo("Jane");
        assertThat(saved.getLastName()).isEqualTo("Smith");
    }
}