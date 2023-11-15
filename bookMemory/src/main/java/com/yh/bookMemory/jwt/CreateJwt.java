package com.yh.bookMemory.jwt;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.yh.bookMemory.entity.Users;

import java.util.Date;

public class CreateJwt {
    public static String createAccessToken(Users user) {
        return JWT.create()
                .withSubject(user.getUserName()) //TODO: withSubject와 withClaim의 차이? 주로 withSubject 에 어떤 값을 넣는지
                .withExpiresAt(new Date(System.currentTimeMillis()+ JwtProperties.ACCESS_EXPIRATION_TIME))
                .withClaim("user_key", user.getUserKey())
                .withClaim("user_email", user.getUserEmail())
                .withClaim("user_name", user.getUserName())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));
    }

    public static String createRefreshToken(Users user, String AccessToken) {
        return JWT.create()
                .withSubject(user.getUserName())
                .withExpiresAt(new Date(System.currentTimeMillis()+ JwtProperties.REFRESH_EXPIRATION_TIME))
                .withClaim("AccessToken", AccessToken) //TODO: 스네이크 케이스로 변경(문제 있을 만한 부분 수정후)
                .withClaim("user_name", user.getUserName())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));
    }
}
