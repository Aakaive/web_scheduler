// ─── 수정 후 ───────────────────────────────────────────────────────────────
package com.aaka.web_scheduler.global.jwt;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.aaka.web_scheduler.global.jwt.JwtProvider;
import com.aaka.web_scheduler.domain.user.repository.UserRepository;
import com.aaka.web_scheduler.domain.user.entity.User;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtProvider jwtProvider, UserRepository userRepository) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {
        // 1) ACCESS_TOKEN 쿠키에서 JWT 추출
        String token = null;
        if (req.getCookies() != null) {
            for (Cookie c : req.getCookies()) {
                if ("ACCESS_TOKEN".equals(c.getName())) {
                    token = c.getValue();
                    break;
                }
            }
        }
        System.out.println(">> JwtFilter 토큰: " + token);
        // 2) 검증 후 SecurityContext에 인증 세팅
        if (token != null && jwtProvider.validateToken(token)) {
            String email = jwtProvider.getEmail(token);
            User user = userRepository.findByEmail(email).orElseThrow();

            System.out.println(">>> JwtFilter VALID    = true, user = " + email);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            System.out.println(">>> JwtFilter AUTH SET = " +
                    SecurityContextHolder.getContext().getAuthentication());
        } else {
            System.out.println(">>> JwtFilter VALID    = false or token null");
        }

        chain.doFilter(req, res);
    }
}
