package com.aaka.web_scheduler.global.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {
    private static final String SECRET = "여기에256비트이상의랜덤문자열을넣으세요";
    private static final Algorithm ALG = Algorithm.HMAC256(SECRET);
    private static final long EXP_MS = 1000 * 60 * 60; // 1시간

    /** Authentication → JWT 발급 */
    public String generateToken(Authentication auth) {
        String email = ((com.aaka.web_scheduler.domain.user.entity.User) auth.getPrincipal()).getEmail();
        return JWT.create()
                .withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXP_MS))
                .sign(ALG);
    }

    /** 토큰 검증 (서명+만료) */
    public boolean validateToken(String token) {
        try {
            ALG.verify(JWT.decode(token));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** 서명은 통과했다고 가정하고, subject(=email) 반환 */
    public String getEmail(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getSubject();
    }
}
