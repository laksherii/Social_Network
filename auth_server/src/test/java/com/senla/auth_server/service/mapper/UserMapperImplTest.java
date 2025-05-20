//package com.senla.auth_server.service.mapper;
//
//import com.senla.auth_server.data.entity.User;
//import com.senla.auth_server.data.entity.User.GenderType;
//import com.senla.auth_server.service.dto.UserDtoRequest;
//import com.senla.auth_server.service.dto.UserDtoResponse;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDate;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class UserMapperImplTest {
//
//    private UserMapperImpl mapper;
//
//    @BeforeEach
//    void setUp() {
//        mapper = new UserMapperImpl();
//    }
//
//    @Test
//    void toUserFromUserDtoRequest_shouldMapAllFields() {
//        // given
//        UserDtoRequest dto = UserDtoRequest.builder()
//                .email("john.doe@example.com")
//                .password("password123")
//                .firstName("John")
//                .lastName("Doe")
//                .birthDay(LocalDate.of(1990, 1, 1))
//                .gender(GenderType.MALE)
//                .build();
//
//        // when
//        User user = mapper.toUserFromUserDtoRequest(dto);
//
//        // then
//        assertThat(user).isNotNull();
//        assertThat(user.getEmail()).isEqualTo(dto.getEmail());
//        assertThat(user.getPassword()).isEqualTo(dto.getPassword());
//        assertThat(user.getFirstName()).isEqualTo(dto.getFirstName());
//        assertThat(user.getLastName()).isEqualTo(dto.getLastName());
//        assertThat(user.getBirthDay()).isEqualTo(dto.getBirthDay());
//        assertThat(user.getGender()).isEqualTo(dto.getGender());
//    }
//
//    @Test
//    void toUserFromUserDtoRequest_shouldReturnNullForNullInput() {
//        // when
//        User user = mapper.toUserFromUserDtoRequest(null);
//
//        // then
//        assertThat(user).isNull();
//    }
//
//    @Test
//    void toUserDtoResponse_shouldMapAllFields() {
//        // given
//        User user = User.builder()
//                .email("jane.doe@example.com")
//                .firstName("Jane")
//                .lastName("Doe")
//                .birthDay(LocalDate.of(1995, 5, 15))
//                .gender(GenderType.FEMALE)
//                .build();
//
//        // when
//        UserDtoResponse dto = mapper.toUserDtoResponse(user);
//
//        // then
//        assertThat(dto).isNotNull();
//        assertThat(dto.getEmail()).isEqualTo(user.getEmail());
//        assertThat(dto.getFirstName()).isEqualTo(user.getFirstName());
//        assertThat(dto.getLastName()).isEqualTo(user.getLastName());
//        assertThat(dto.getBirthDay()).isEqualTo(user.getBirthDay());
//        assertThat(dto.getGender()).isEqualTo(user.getGender());
//    }
//
//    @Test
//    void toUserDtoResponse_shouldReturnNullForNullInput() {
//        // when
//        UserDtoResponse dto = mapper.toUserDtoResponse(null);
//
//        // then
//        assertThat(dto).isNull();
//    }
//}
