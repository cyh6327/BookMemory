package com.yh.bookMemory.service;

import com.yh.bookMemory.BookMemoryApplication;
import com.yh.bookMemory.dto.UserDTO;
import com.yh.bookMemory.entity.Users;
import com.yh.bookMemory.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/*
@ExtendWith :
    - 단위 테스트에 공통적으로 사용할 확장 기능을 선언해주는 역할을 한다. 인자로 확장할 Extension을 명시하면 된다.
    - @SpringBootConfiguration이 정의된 클래스의 패키지를 스캔하여 빈을 등록한다
    - @SpringBootTest를 사용하면 application context를 전부 로딩하기 때문에 무거워진다.
        해당 어노테이션을 사용하면 Junit4에서 @Autowired, @MockBean에 해당하는 컨텍스트만 로딩하여 테스트를 진행하게 된다.
    - Junit4의 @RunWith(SpringRunner.class)와 동일
SpringExtension :
    - Spring 테스트를 쉽게할 수 있도록 도와주는 Extension
    - 이를 이용하면 Spring 테스트에 사용할 ApplicationContext를 쉽게 생성할 수 있다.
*/
@ExtendWith(SpringExtension.class)
/*
@DataJpaTest :
    - 단위 테스트가 끝날 때 마다 자동으로 DB를 롤백시켜준다. (@Transactional 내장)
    - 내장된 메모리 데이터베이스를 이용해 테스트를 실행
        @AutoConfigureTestDatabase 를 사용하면 설정을 재정의 할 수 있다.
 */
@DataJpaTest
//EmbeddedDatabase(내장된 데이터베이스)를 사용하지 않게 변경 => 사용중인 DataSource 등록
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Log4j2
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    private static Users user;
    private static UserDTO userDTO;

    private Long testKey = 1L;
    private String testEmail = "cyh6327@gmail.com";

    // 테스트 실행 전 실행
    @BeforeEach
    void setup() {
        user = Users.builder()
                .userEmail("test@gmail.com")
                .userName("test")
                .refreshToken(null)
                .build();

        userDTO = UserDTO.builder()
                .userEmail("test@google.com")
                .userName("test")
                .refreshToken(null)
                .build();
    }

    @DisplayName("이메일로 user 엔티티 가져오기")
    @Test
    void getUserInfoByUserEmailTest() {
        Users user = userRepository.getReferenceByUserEmail(testEmail);

        assertNotNull(user);
    }

    @DisplayName("user_key로 user 엔티티 가져오기")
    @Test
    void getUserInfoByUserKeyTest() {
        Users user = userRepository.getReferenceByUserKey(testKey);

        assertNotNull(user);
    }

    @DisplayName("user 엔티티 생성")
    @Test
    void createUserTest() {
        Users saveUser = userRepository.saveAndFlush(user);
        assertNotNull(saveUser);
        assertEquals(user.getUserKey(),saveUser.getUserKey());
    }

    @DisplayName("user refresh token 업데이트")
    @Test
    void updateRefreshTokenTest() {
//        Long userKey = getUserKeyFromJwt();
//        Users user = getUserInfoByUserKey(userKey);
//
//        UserDTO userDto = userEntityToDto(user);
//        userDto.setRefreshToken(refreshToken);
//
//        user = userDtoToEntitiy(userDto);
//
//        Users result = userRepository.saveAndFlush(user);
//
//        return result;
    }
}