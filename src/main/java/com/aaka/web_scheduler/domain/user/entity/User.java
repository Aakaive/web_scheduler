package com.aaka.web_scheduler.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String name;

    public User(String email, String name) {
        this.email = email;
        this.name  = name;
    }
}
