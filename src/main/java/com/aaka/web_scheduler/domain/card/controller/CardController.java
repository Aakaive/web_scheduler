package com.aaka.web_scheduler.domain.card.controller;

import com.aaka.web_scheduler.domain.card.dto.CardRequestDto;
import com.aaka.web_scheduler.domain.card.dto.CardResponseDto;
import com.aaka.web_scheduler.domain.card.dto.CalendarCountDto;
import com.aaka.web_scheduler.domain.card.service.CardService;
import com.aaka.web_scheduler.domain.user.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces/{wsId}/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    /** 1) 카드 생성 */
    @PostMapping
    public CardResponseDto create(
            @PathVariable Long wsId,
            @RequestBody CardRequestDto dto,
            @AuthenticationPrincipal User user
    ) {
        System.out.println("컨트롤러 호출");
        return cardService.create(wsId, dto, user);
    }

    /** 2) 카드보드 조회 */
    @GetMapping
    public List<CardResponseDto> listBoard(
            @PathVariable Long wsId,
            @AuthenticationPrincipal User user
    ) {
        return cardService.listBoard(wsId, user);
    }

    /** 3) 달력형 조회 */
    @GetMapping("/calendar")
    public List<CalendarCountDto> listCalendar(
            @PathVariable Long wsId,
            @AuthenticationPrincipal User user,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return cardService.listCalendar(wsId, user, year, month);
    }

    /** 4) 순서 변경 */
    @PatchMapping("/reorder")
    public void reorder(
            @PathVariable Long wsId,
            @RequestBody List<Long> orderedIds,
            @AuthenticationPrincipal User user
    ) {
        cardService.reorder(wsId, orderedIds, user);
    }
}