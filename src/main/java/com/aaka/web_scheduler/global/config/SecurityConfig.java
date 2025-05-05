package com.aaka.web_scheduler.global.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseCookie;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.aaka.web_scheduler.global.jwt.JwtAuthenticationFilter;
import com.aaka.web_scheduler.global.jwt.JwtProvider;
import com.aaka.web_scheduler.domain.user.repository.UserRepository;
import com.aaka.web_scheduler.auth.oauth.OAuth2LoginSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    public SecurityConfig(
            JwtProvider jwtProvider,
            UserRepository userRepository,
            OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler
    ) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtProvider, userRepository);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS / CSRF
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                // 인증·인가
                .authorizeHttpRequests(auth -> auth
                        // OAuth2 진입점과 콜백은 인증 없이 열어두기
                        .requestMatchers(
                                "/oauth2/authorization/**",
                                "/login/oauth2/code/**"
                        ).permitAll()
                        // 로그아웃은 인증된 사용자만
                        .requestMatchers(HttpMethod.POST, "/api/auth/logout")
                        .authenticated()
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // OAuth2 로그인 활성화
                .oauth2Login(oauth -> oauth
                        // **커스텀 성공 핸들러만 사용** (defaultTargetUrl 사용 X)
                        .successHandler(oAuth2LoginSuccessHandler)
                )

                // JWT 필터
                .addFilterBefore(
                        jwtAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class
                )
        ;

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration c = new CorsConfiguration();
            c.setAllowedOrigins(List.of("http://localhost:3000"));
            c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE"));
            c.setAllowedHeaders(List.of("*"));
            c.setAllowCredentials(true);
            return c;
        };
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry reg) {
                reg.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowCredentials(true)
                        .allowedMethods("*");
            }
        };
    }
}
