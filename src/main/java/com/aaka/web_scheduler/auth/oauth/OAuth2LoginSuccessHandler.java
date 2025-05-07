package com.aaka.web_scheduler.auth.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import com.aaka.web_scheduler.global.jwt.JwtProvider;
import com.aaka.web_scheduler.domain.user.entity.User;
import com.aaka.web_scheduler.domain.user.repository.UserRepository;

@Service
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepo;

    public OAuth2LoginSuccessHandler(
            JwtProvider jwtProvider,
            UserRepository userRepo,
            @Value("${app.frontend.base-url}") String frontendBaseUrl,
            @Value("${app.frontend.workspace-path}") String workspacePath
    ) {
        // application.yml 에 정의한 frontendBaseUrl + workspacePath 로 리다이렉트
        super(frontendBaseUrl + workspacePath);
        setAlwaysUseDefaultTargetUrl(true);

        this.jwtProvider = jwtProvider;
        this.userRepo    = userRepo;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest  request,
            HttpServletResponse response,
            Authentication      authentication
    ) throws IOException, ServletException {

        // 1) OAuth2User 에서 이메일·이름 추출
        var oauthUser = (org.springframework.security.oauth2.core.user.DefaultOAuth2User)
                authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String name  = oauthUser.getAttribute("name");

        // 2) DB에 없는 유저라면 저장
        User user = userRepo.findByEmail(email)
                .orElseGet(() ->
                        userRepo.save(User.builder()
                                .email(email)
                                .name(name)
                                .build())
                );

        // 3) JWT 생성 후 쿠키에 담기
        String token = jwtProvider.generateToken(email);
        Cookie cookie = new Cookie("ACCESS_TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);  // HTTPS 환경이 아니라면 false 로 설정
        cookie.setPath("/");
        // getExpirationMillis() 는 JwtProvider 에서 @Value 로 주입된 만료시간(ms)을 반환
        cookie.setMaxAge((int)(jwtProvider.getExpirationMillis() / 1000));
        response.addCookie(cookie);

        // 4) 프론트(/workspace)로 리다이렉트
        super.onAuthenticationSuccess(request, response, authentication);
    }
}