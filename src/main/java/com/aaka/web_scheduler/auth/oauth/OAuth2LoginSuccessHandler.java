package com.aaka.web_scheduler.auth.oauth;

import java.io.IOException;
import java.time.Duration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.aaka.web_scheduler.global.jwt.JwtProvider;
import com.aaka.web_scheduler.domain.user.repository.UserRepository;
import com.aaka.web_scheduler.domain.user.entity.User;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    public OAuth2LoginSuccessHandler(JwtProvider jwtProvider,
                                     UserRepository userRepository) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req,
                                        HttpServletResponse res,
                                        Authentication auth) throws IOException {
        // 1) OAuth2User 에서 이메일 꺼내기
        String email;
        Object principal = auth.getPrincipal();
        if (principal instanceof OAuth2User) {
            email = ((OAuth2User) principal).getAttribute("email");
        } else {
            // 혹시 다른 유형의 Principal 이 올 경우 대비
            email = auth.getName();
        }

        // 2) DB에 유저가 없다면 저장 (CustomOAuth2UserService 에서 이미 저장했더라도 안전하게)
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(new User(email, null)));

        // 3) 이메일 기반으로 JWT 생성
        String token = jwtProvider.generateToken(email);

        // 4) httpOnly 쿠키에 담기
        ResponseCookie cookie = ResponseCookie.from("ACCESS_TOKEN", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofHours(1))
                .sameSite("Lax")
                .build();
        res.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // 5) 성공 응답 (리다이렉트 없이 JSON)
        res.setContentType("application/json");
        res.getWriter().write("{\"status\":\"ok\"}");
        res.getWriter().flush();
    }
}
