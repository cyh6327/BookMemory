package com.yh.bookMemory.jwt;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.yh.bookMemory.dto.UserDTO;
import com.yh.bookMemory.entity.Users;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.IOException;

import static org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JwtTokenTest {
    private static Users user;
    private final String SECRET_KEY = JwtProperties.SECRET;
    private static String accessToken;
    // 만료일이 없는 jwt token
    private static String noExpireJwt = "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0IiwidXNlcl9rZXkiOjEwMDAsInVzZXJfZW1haWwiOiJ0ZXN0QGdtYWlsLmNvbSIsInVzZXJfbmFtZSI6InRlc3QifQ.1GPWLipeaHBGzNmAk9p2CRqGvL5dYWSBO4BDVDU6PwCAIb3P1DvD92fugTA_0-fuVfDSFkbFCEbygyLkB8d4CA";
    private static DecodedJWT decodedJWT;

    // 테스트 실행 전 실행
    @BeforeEach
    void setup() {
        user = Users.builder()
                .userKey(999L)
                .userEmail("test@gmail.com")
                .userName("test")
                .refreshToken(null)
                .build();
    }

    @Order(1)
    @DisplayName("JwtTokenVerifier 객체 생성")
    @Test
    void createJwtTokenVerifierTest() {
        JwtTokenVerifier jwtTokenVerifier = new JwtTokenVerifier(SECRET_KEY);
        assertNotNull(jwtTokenVerifier);
    }

    @Order(2)
    @DisplayName("Jwt access token 생성")
    @Test
    void createAccessTokenTest() {
        accessToken = CreateJwt.createAccessToken(user);
        assertNotNull(accessToken);
        log.info("accessToken......................"+accessToken);
    }

    @Order(3)
    @DisplayName("Jwt refresh token 생성")
    @Test
    void createRefreshTokenTest() {
        String refreshToken = CreateJwt.createRefreshToken(user, accessToken);
        assertNotNull(refreshToken);
        log.info("refreshToken......................"+refreshToken);
    }

    @Order(4)
    @DisplayName("JWT 토큰 검증")
    @Test
    void verifyJwtTokenTest() throws IOException {
        //test.properties 의 더미 access_token 값 꺼내오는 코드
//        ConfigurableApplicationContext context = new AnnotationConfigApplicationContext();
//        ConfigurableEnvironment environment = context.getEnvironment();
//        MutablePropertySources propertySources = environment.getPropertySources();
//        propertySources.addLast(new ResourcePropertySource("classpath:test.properties"));

//        String accessToken = environment.getProperty("access.token");
        log.info("accessToken.................."+accessToken);

        JwtTokenVerifier jwtTokenVerifier = new JwtTokenVerifier(JwtProperties.SECRET);
        decodedJWT = jwtTokenVerifier.verify(accessToken);

        assertNotNull(decodedJWT);
    }

    @Order(5)
    @DisplayName("JWT 토큰에서 user 정보 가져오기")
    @Test
    void getJwtInfoTest() {
        String user_name = decodedJWT.getSubject();
        Long user_key = decodedJWT.getClaim("user_key").asLong();

        log.info("user_name............................."+user_name);
        log.info("user_key............................."+user_key);

        assertNotNull(user_key);
    }

    @Order(6)
    @DisplayName("JWT 토큰에서 user_key 가져오기")
    @Test
    void getUserKeyFromJwtTest() {
    }
}