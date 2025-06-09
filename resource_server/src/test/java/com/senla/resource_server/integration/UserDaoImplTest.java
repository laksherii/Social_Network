package com.senla.resource_server.integration;

import com.senla.resource_server.data.dao.impl.UserDaoImpl;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.data.entity.User.GenderType;
import com.senla.resource_server.data.entity.User.RoleType;
import com.senla.resource_server.data.entity.Wall;
import com.senla.resource_server.service.dto.user.UserSearchDto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {UserDaoImpl.class, TestDataConfig.class})
public class UserDaoImplTest extends BaseIntegrationTest {

    @Autowired
    private UserDaoImpl userDao;

    @Test
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:userDao/insert_user.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void findById() {
        Optional<User> found = userDao.findById(1L);
        assertThat(found).isPresent();
    }

    @Test
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:userDao/insert_user.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void findByID_shouldReturnEmptyIfUserNotFound() {
        // when
        Optional<User> result = userDao.findById(1000L);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:userDao/insert_user.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void findByEmail() {
        Optional<User> found = userDao.findByEmail("john.doe@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("first_name");
    }

    @Test
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:userDao/insert_user.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void findByEmail_shouldReturnEmptyIfUserNotFound() {
        // when
        Optional<User> result = userDao.findByEmail("notfound@example.com");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    @Transactional
    public void save() {

        User testUser = User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .birthDay(LocalDate.of(1992, 8, 20))
                .enabled(true)
                .gender(GenderType.FEMALE)
                .role(RoleType.ROLE_ADMIN)
                .build();

        Wall wall = Wall.builder()
                .owner(testUser)
                .build();

        testUser.setWall(wall);

        User saved = userDao.save(testUser);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("jane.smith@example.com");
        assertThat(saved.getFirstName()).isEqualTo("Jane");
        assertThat(saved.getLastName()).isEqualTo("Smith");
    }

    @Test
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:userDao/insert_user.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    @Transactional
    public void update() {

        User userFromDb = userDao.findById(1L).orElseThrow(() -> new RuntimeException("User not found"));
        userFromDb.setFirstName("UpdatedName");
        User updated = userDao.update(userFromDb);

        assertThat(updated.getFirstName()).isEqualTo("UpdatedName");
    }

    @Test
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:userDao/insert_user.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void searchUser_byFirstAndLastName() {
        UserSearchDto userSearchDto = UserSearchDto.builder()
                .firstName("first_name")
                .lastName("last_name")
                .build();

        List<User> results = userDao.searchUser(userSearchDto);
        assertThat(results)
                .extracting(User::getEmail)
                .contains("john.doe@example.com");
    }

    @Test
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:userDao/insert_user.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void searchUser_shouldReturnUsersWithMatchingLastName_only() {
        // given
        UserSearchDto userSearchDto = new UserSearchDto();
        userSearchDto.setFirstName("");
        userSearchDto.setLastName("last_name");

        // when
        List<User> results = userDao.searchUser(userSearchDto);

        // then
        assertThat(results)
                .extracting(User::getLastName)
                .contains("last_name");
    }

    @Test
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:userDao/insert_user.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void searchUser_shouldReturnUsersWithMatchingFirstName_only() {
        // given
        UserSearchDto userSearchDto = new UserSearchDto();
        userSearchDto.setFirstName("first_name");
        userSearchDto.setLastName("");

        // when
        List<User> results = userDao.searchUser(userSearchDto);

        // then
        assertThat(results)
                .hasSize(1)
                .extracting(User::getFirstName)
                .contains("first_name");
    }

    @Test
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:userDao/insert_user.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void searchUser_byAge() {
        int expectedAge = LocalDate.now().getYear() - 1992;

        UserSearchDto dto = new UserSearchDto();
        dto.setAge(expectedAge);

        List<User> results = userDao.searchUser(dto);
        assertThat(results)
                .extracting(User::getEmail)
                .contains("john.doe@example.com");
    }

    @Test
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:userDao/insert_user.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:truncate_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void searchUser_byGender() {
        UserSearchDto dto = new UserSearchDto();
        dto.setGender(GenderType.MALE);

        List<User> results = userDao.searchUser(dto);
        assertThat(results)
                .extracting(User::getGender)
                .containsOnly(GenderType.MALE);
    }
}
