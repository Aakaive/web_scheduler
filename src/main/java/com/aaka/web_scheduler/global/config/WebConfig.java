package com.aaka.web_scheduler.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                // /api/** 에만 CORS 적용
                .addMapping("/api/**")
                // Next.js(프론트) 주소
                .allowedOrigins("http://localhost:3000")
                // 허용할 HTTP 메서드
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                // 허용할 헤더
                .allowedHeaders("*")
                // 쿠키 전송 허용
                .allowCredentials(true);
    }
}