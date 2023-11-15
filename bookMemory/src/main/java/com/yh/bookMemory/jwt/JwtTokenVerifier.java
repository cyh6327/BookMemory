package com.yh.bookMemory.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.yh.bookMemory.dto.BookInfoDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class JwtTokenVerifier {
    private final JWTVerifier verifier;

    public JwtTokenVerifier(String secret) {
        Algorithm algorithm = Algorithm.HMAC512(secret);
        verifier = JWT.require(algorithm).build();
    }

    public DecodedJWT verify(String token) throws JWTVerificationException {
        return verifier.verify(token);
    }

    public DecodedJWT decodeJWT(String receivedToken) {
        JwtTokenVerifier jwtTokenVerifier = new JwtTokenVerifier(JwtProperties.SECRET);
        DecodedJWT decodedJWT = jwtTokenVerifier.verify(receivedToken);

        return decodedJWT;
    }

    public String getJwtInfo(String receivedToken, String key) {
        DecodedJWT jwt = decodeJWT(receivedToken);
        if(key == "user_name") {
            return (String)jwt.getSubject();
        } else {
            return jwt.getClaim(key).asString();
        }
    }

    public ResponseEntity<String> getAccessTokenInfo(String receivedToken) {
        String accessToken = null;
        Date accessTokenExp = null;
        String socialAccessToken = null;
        Long userKey = null;
        String userName = null;
        String email = null;

        try {
            DecodedJWT decodedJWT = decodeJWT(receivedToken);
//            if (decodedJWT.getClaim("email") != null) {
            if(decodedJWT != null) {
                accessToken = receivedToken;
                accessTokenExp = decodedJWT.getExpiresAt();
                userName = (String)decodedJWT.getSubject();
                userKey = decodedJWT.getClaim("user_key").asLong();
                email = decodedJWT.getClaim("user_email").asString();
                socialAccessToken = decodedJWT.getClaim("socialAccessToken").asString();

                log.info("accessToken......................"+accessToken);
                log.info("accessTokenExp......................"+accessTokenExp);
                log.info("socialAccessToken......................"+socialAccessToken);
                log.info("userName......................"+userName);
                log.info("userKey......................"+userKey);
                log.info("email......................"+email);
                log.info("socialAccessToken......................"+socialAccessToken);
            } else {
                throw new JWTVerificationException(receivedToken);
            }
        } catch (JWTVerificationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String verifiedJwt = JWT.create()
                .withSubject(userName)
                .withExpiresAt(accessTokenExp)
                .withClaim("user_key", userKey)
                .withClaim("user_email", email)
                .withClaim("user_name", userName)
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        log.info("verifiedJwt......................"+verifiedJwt);

//        Map<String,Object> resultMap = new HashMap<>();
//        resultMap.put("verifiedJwt", verifiedJwt);
        //resultMap.put("userKey",userKey);

        return ResponseEntity.ok(verifiedJwt);
    }
}
