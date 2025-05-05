// ─── 수정 전 ───────────────────────────────────────────────────────────────
package com.aaka.web_scheduler.auth.oauth;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import com.aaka.web_scheduler.global.jwt.JwtProvider;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req,
                                        HttpServletResponse res,
                                        Authentication auth) throws IOException {
        String token = jwtProvider.generateToken(auth);
        res.setContentType("application/json");
        res.getWriter().write("{\"token\":\"" + token + "\"}");
        res.getWriter().flush();
    }
}
