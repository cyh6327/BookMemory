package com.yh.bookMemory.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.yh.bookMemory.dto.BookInfoDTO;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
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

    // @param ignore : true - 만료됐어도 무시하고 리턴 false - 만료됐다면 예외던지기
    public DecodedJWT verify(String token, boolean ignore) throws TokenExpiredException {
        if(ignore) {
            try {
                verifier.verify(token);
            } catch(TokenExpiredException e) {
                return verifier.verify(token);
            }
        }
        return verifier.verify(token);
    }

    public Object getJwtInfo(String receivedToken, String key) {
        JwtTokenVerifier jwtTokenVerifier = new JwtTokenVerifier(JwtProperties.SECRET);
        DecodedJWT jwt = jwtTokenVerifier.verify(receivedToken, true);

        log.info("getJwtInfo................................"+jwt.toString());
        if(key == "user_name") {
            return jwt.getSubject();
        } else {
            log.info("jwt.getClaim(key)................................"+jwt.getClaim(key));
            // jwt.getClaim(key).asString() 으로 리턴하면 왜 null로 리턴되는 거지?
            return jwt.getClaim(key);
        }
    }

    //
    public ResponseEntity<String> getJwtTokenInfo(String receivedToken) {
        String accessToken = null;
        Date accessTokenExp = null;
        String socialAccessToken = null;
        Long userKey = null;
        String userName = null;
        String email = null;

        DecodedJWT decodedJWT = verify(receivedToken, true);
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

            Date now = new Date();
            boolean isExpired = accessTokenExp.before(now);

            if(isExpired) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                //throw new TokenExpiredException("The Access Token has expired.", accessTokenExp.toInstant());
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
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
