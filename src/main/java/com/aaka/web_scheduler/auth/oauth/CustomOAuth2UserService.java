package com.aaka.web_scheduler.auth.oauth;

import com.aaka.web_scheduler.domain.user.entity.User;
import com.aaka.web_scheduler.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepo;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) throws OAuth2AuthenticationException {
        // 1. 구글에서 사용자 정보 받아오기
        OAuth2User oUser = super.loadUser(req);
        String email = oUser.getAttribute("email");
        String name  = oUser.getAttribute("name");

        // 2. DB에 이미 없으면 새 User 생성
        userRepo.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)    // ← 하드코딩이 아니라 변수 사용
                            .name(name)
                            .build();
                    return userRepo.save(newUser);
                });

        // 3. OAuth2User 그대로 반환 (추후 AuthenticationSuccessHandler 에서 JWT 발급)
        return oUser;
    }
}
