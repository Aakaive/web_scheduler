package com.aaka.web_scheduler.global.jwt;

import com.auth0.jwt.*;
import com.auth0.jwt.algorithms.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;
    private final long EXP_MS = 1000 * 60 * 60; // 1시간

    public String createToken(String email) {
        Date now = new Date();
        return JWT.create()
                .withSubject(email)
                .withIssuedAt(now)
                .withExpiresAt(new Date(now.getTime() + EXP_MS))
                .sign(Algorithm.HMAC512(secret));
    }

    public String getEmail(String token) {
        return JWT.require(Algorithm.HMAC512(secret))
                .build().verify(token).getSubject();
    }

    public boolean validate(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
