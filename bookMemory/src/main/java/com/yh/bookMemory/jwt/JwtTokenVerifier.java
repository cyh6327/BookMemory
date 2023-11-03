package com.yh.bookMemory.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class JwtTokenVerifier {
    private final JWTVerifier verifier;

    public JwtTokenVerifier(String secret) {
        Algorithm algorithm = Algorithm.HMAC512(secret);
        verifier = JWT.require(algorithm).build();
    }

    public DecodedJWT verify(String token) throws JWTVerificationException {
        return verifier.verify(token);
    }

    public String getAccessTokenInfo(String receivedToken) {
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
            } else {
                throw new JWTVerificationException(receivedToken);
            }

        } catch (JWTVerificationException ex) {
            throw new JWTVerificationException(receivedToken);
        }

        return JWT.create()
                .withSubject(userName)
                .withExpiresAt(accessTokenExp)
                .withClaim("email", email)
                .withClaim("username", userName)
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));
    }
}
