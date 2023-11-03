package com.yh.bookMemory.jwt;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.yh.bookMemory.entity.Users;

import java.util.Date;

public class CreateJwt {
    public static String createAccessToken(Users user) {
        return JWT.create()
                .withSubject(user.getUserName())
                .withExpiresAt(new Date(System.currentTimeMillis()+ JwtProperties.ACCESS_EXPIRATION_TIME))
                .withClaim("email", user.getUserEmail())
                .withClaim("username", user.getUserName())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));
    }

    public static String createRefreshToken(Users user, String AccessToken) {
        return JWT.create()
                .withSubject(user.getUserName())
                .withExpiresAt(new Date(System.currentTimeMillis()+ JwtProperties.REFRESH_EXPIRATION_TIME))
                .withClaim("AccessToken", AccessToken)
                .withClaim("username", user.getUserName())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));
    }
}
