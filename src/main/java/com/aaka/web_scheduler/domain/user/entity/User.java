// src/main/java/com/aaka/web_scheduler/domain/user/entity/User.java
package com.aaka.web_scheduler.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String name;

    // 비밀번호는 OAuth만 쓰거나 직접 로그인 안 쓰실 경우 빈값으로
    private String password;

    // 계정 활성화 상태
    private boolean enabled = true;

    ////////////////////////////////
    // UserDetails 인터페이스 메서드
    ////////////////////////////////
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 롤이 없으면 빈 리스트, 있으면 여기서 return List.of(new SimpleGrantedAuthority("ROLE_USER")) 등
        return Collections.emptyList();
    }

    @Override
    public String getUsername() {
        // Spring Security에서는 유저를 식별할 때 이 값을 씁니다.
        return this.email;
    }

    @Override
    public String getPassword() {
        // OAuth 전용이면 빈 문자열도 괜찮습니다.
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // 만료 개념 없으면 true
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // 잠금 기능 없으면 true
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // 자격증명 만료 개념 없으면 true
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public User(String email, String name) {
        this.email = email;
        this.name  = name;
    }
}
