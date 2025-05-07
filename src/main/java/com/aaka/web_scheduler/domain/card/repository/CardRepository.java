package com.aaka.web_scheduler.domain.card.repository;

import com.aaka.web_scheduler.domain.card.entity.Card;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    // 1) 카드보드 조회 (위치 순)
    List<Card> findByWorkspaceIdOrderByPositionAsc(Long workspaceId);

    // 2) 달력 조회: 특정 기간 사이에 scheduledAt 있는 카드
    List<Card> findByWorkspaceIdAndScheduledAtBetween(
            Long workspaceId,
            LocalDateTime start,
            LocalDateTime end
    );

    // 3) 새 카드 위치 계산용: 현재 max(position)
    @Query("SELECT COALESCE(MAX(c.position), 0) FROM Card c WHERE c.workspace.id = :wsId")
    Integer findMaxPosition(@Param("wsId") Long workspaceId);
}