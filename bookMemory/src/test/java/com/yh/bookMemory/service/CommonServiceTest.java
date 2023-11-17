package com.yh.bookMemory.service;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.yh.bookMemory.jwt.JwtTokenVerifier;
import org.hibernate.cfg.Environment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class CommonServiceTest {

    @DisplayName("jwt token에서 user_key 가져오기")
    @Test
    void getUserKeyFromJwtTest() throws IOException {


    }

}