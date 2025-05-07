// CardRequestDto.java
package com.aaka.web_scheduler.domain.card.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record CardRequestDto(
        @NotBlank String title,
        String description,
        @NotNull LocalDateTime scheduledAt,
        boolean reminder
) {}