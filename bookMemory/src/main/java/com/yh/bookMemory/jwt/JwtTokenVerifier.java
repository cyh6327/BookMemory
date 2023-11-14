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
import java.util.List;

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

    public ResponseEntity<Object> getAccessTokenInfo(String receivedToken) {
        String accessToken = null;
        Date accessTokenExp = null;
        String socialAccessToken = null;
        String userName = null;
        String email = null;

        try {
            JwtTokenVerifier jwtTokenVerifier = new JwtTokenVerifier(JwtProperties.SECRET);
            DecodedJWT decodedJWT = jwtTokenVerifier.verify(receivedToken);

            if (decodedJWT.getClaim("email") != null) {
                accessToken = receivedToken;
                accessTokenExp = decodedJWT.getExpiresAt();
                userName = (String)decodedJWT.getSubject();
                email = decodedJWT.getClaim("email").asString();
                socialAccessToken = decodedJWT.getClaim("socialAccessToken").asString();

                log.info("accessToken......................"+accessToken);
                log.info("accessTokenExp......................"+accessTokenExp);
                log.info("userName......................"+userName);
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
                .withClaim("email", email)
                .withClaim("username", userName)
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        log.info("verifiedJwt......................"+verifiedJwt);

        return ResponseEntity.ok(verifiedJwt);
    }
}
