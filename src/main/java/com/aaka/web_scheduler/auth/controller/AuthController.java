package com.aaka.web_scheduler.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * 로그인 상태 확인
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> status() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuth = auth != null
                && auth.isAuthenticated()
                && !(auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken);
        return ResponseEntity.ok(Map.of("authenticated", isAuth));
    }

    /**
     * 로그아웃: 세션 무효화 + 쿠키 삭제
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request,
                                       HttpServletResponse response) {
        // 1) 세션 무효화
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();

        // 2) ACCESS_TOKEN 쿠키 삭제
        Cookie jwtCookie = new Cookie("ACCESS_TOKEN", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false); // 로컬 http 테스트 시 false
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);

        // 3) JSESSIONID 쿠키 삭제
        Cookie jsession = new Cookie("JSESSIONID", null);
        jsession.setHttpOnly(true);
        jsession.setPath("/");
        jsession.setMaxAge(0);
        response.addCookie(jsession);

        return ResponseEntity.ok().build();
    }
}