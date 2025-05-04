package com.aaka.web_scheduler.auth.oauth;

import com.aaka.web_scheduler.domain.user.entity.User;
import com.aaka.web_scheduler.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepo;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) throws OAuth2AuthenticationException {
        OAuth2User oUser = super.loadUser(req);
        String email = oUser.getAttribute("email");
        String name  = oUser.getAttribute("name");

        userRepo.findByEmail(email)
                .orElseGet(() -> userRepo.save(new User(email, name)));

        return oUser;
    }
}
