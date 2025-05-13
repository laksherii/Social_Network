package com.senla.resource_server.integretion;

import com.senla.resource_server.data.dao.UserDao;
import com.senla.resource_server.data.entity.User;
import com.senla.resource_server.data.entity.User.GenderType;
import com.senla.resource_server.data.entity.User.RoleType;
import com.senla.resource_server.data.entity.Wall;
import com.senla.resource_server.service.dto.user.UserSearchDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class UserDaoImplTest extends BaseIntegrationTest {

    @Autowired
    private UserDao dao;

    private User testUser;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    public void setup() {
        testUser = new User();
        testUser.setFirstName("Admin");
        testUser.setLastName("Admin");
        testUser.setEmail("john.doe@example.com");
        testUser.setBirthDay(LocalDate.of(1992, 3, 20));
        testUser.setEnabled(true);
        testUser.setGender(GenderType.FEMALE);
        testUser.setRole(RoleType.ROLE_ADMIN);

        Wall wall = Wall.builder().build();
        wall.setOwner(testUser);
        testUser.setWall(wall);

        entityManager.persist(testUser);
        entityManager.flush();
    }

    @Test
    public void findById() {
        Optional<User> found = dao.findById(testUser.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(testUser.getEmail());
    }

    @Test
    public void findByEmail() {
        Optional<User> found = dao.findByEmail("john.doe@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Admin");
    }

    @Test
    @Transactional
    public void save() {

        User testUser = new User();
        testUser.setFirstName("Jane");
        testUser.setLastName("Smith");
        testUser.setEmail("jane.smith@example.com");
        testUser.setBirthDay(LocalDate.of(1992, 8, 20));
        testUser.setEnabled(true);
        testUser.setGender(GenderType.FEMALE);
        testUser.setRole(RoleType.ROLE_ADMIN);

        Wall wall = Wall.builder().build();
        wall.setOwner(testUser);
        testUser.setWall(wall);

        User saved = dao.save(testUser);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("jane.smith@example.com");
        assertThat(saved.getFirstName()).isEqualTo("Jane");
        assertThat(saved.getLastName()).isEqualTo("Smith");

    }

    @Test
    @Transactional
    public void update() {
        testUser.setFirstName("UpdatedName");
        User updated = dao.update(testUser);
        assertThat(updated.getFirstName()).isEqualTo("UpdatedName");

        User reloaded = entityManager.find(User.class, updated.getId());
        assertThat(reloaded.getFirstName()).isEqualTo("UpdatedName");
    }

    @Test
    public void searchUser_byFirstAndLastName() {
        UserSearchDto dto = new UserSearchDto();
        dto.setFirstName("Admin");
        dto.setLastName("Admin");

        List<User> results = dao.searchUser(dto);
        assertThat(results)
                .extracting(User::getEmail)
                .contains("john.doe@example.com");
    }

    @Test
    public void searchUser_byAge() {
        int expectedAge = LocalDate.now().getYear() - 1992;

        UserSearchDto dto = new UserSearchDto();
        dto.setAge(expectedAge);

        List<User> results = dao.searchUser(dto);
        assertThat(results)
                .extracting(User::getEmail)
                .contains("john.doe@example.com");
    }

    @Test
    public void searchUser_byGender() {
        UserSearchDto dto = new UserSearchDto();
        dto.setGender(GenderType.MALE);

        List<User> results = dao.searchUser(dto);
        assertThat(results)
                .extracting(User::getGender)
                .containsOnly(GenderType.MALE);
    }
}