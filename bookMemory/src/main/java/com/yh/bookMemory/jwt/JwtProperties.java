package com.yh.bookMemory.jwt;

public interface JwtProperties {
    String SECRET = "dusgygmldnjs"; // 우리 서버만 알고 있는 비밀값
    int ACCESS_EXPIRATION_TIME = 600000; // 10분
    int REFRESH_EXPIRATION_TIME = 604800000;    // 7일
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
}
