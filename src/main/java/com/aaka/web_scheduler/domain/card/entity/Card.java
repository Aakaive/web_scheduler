package com.aaka.web_scheduler.domain.card.entity;

import com.aaka.web_scheduler.domain.workspace.entity.Workspace;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "card")
public class Card {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Column(nullable = false)
    private boolean reminder;   // 리마인드 선택 여부

    @Column(nullable = false)
    private Integer position;   // 카드보드 순서를 위한 위치 값

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    // 기본 생성자, getter/setter 생략. 롬복 쓰시면 @Getter @Setter @NoArgsConstructor @AllArgsConstructor 추가
}