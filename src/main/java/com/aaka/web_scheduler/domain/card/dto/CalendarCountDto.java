// CalendarCountDto.java
package com.aaka.web_scheduler.domain.card.dto;

import java.time.LocalDate;
import java.util.List;

public record CalendarCountDto(
        LocalDate date,          // YYYY-MM-DD
        int count,               // 그 날 카드 수
        List<CardResponseDto> cards
) {}