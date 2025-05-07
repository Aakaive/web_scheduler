// CardResponseDto.java
package com.aaka.web_scheduler.domain.card.dto;

import com.aaka.web_scheduler.domain.card.entity.Card;
import java.time.LocalDateTime;

public record CardResponseDto(
        Long id,
        String title,
        String description,
        LocalDateTime scheduledAt,
        boolean reminder,
        Integer position
) {
    public CardResponseDto(Card c) {
        this(
                c.getId(),
                c.getTitle(),
                c.getDescription(),
                c.getScheduledAt(),
                c.isReminder(),
                c.getPosition()
        );
    }
}