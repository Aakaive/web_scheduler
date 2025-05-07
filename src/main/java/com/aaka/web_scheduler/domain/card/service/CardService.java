package com.aaka.web_scheduler.domain.card.service;

import com.aaka.web_scheduler.domain.card.dto.CardRequestDto;
import com.aaka.web_scheduler.domain.card.dto.CardResponseDto;
import com.aaka.web_scheduler.domain.card.dto.CalendarCountDto;
import com.aaka.web_scheduler.domain.card.entity.Card;
import com.aaka.web_scheduler.domain.card.repository.CardRepository;
import com.aaka.web_scheduler.domain.user.entity.User;
import com.aaka.web_scheduler.domain.workspace.repository.WorkspaceRepository;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CardService {

    private final CardRepository cardRepo;
    private final WorkspaceRepository wsRepo;

    public CardService(
            CardRepository cardRepo,
            WorkspaceRepository wsRepo
    ) {
        this.cardRepo = cardRepo;
        this.wsRepo = wsRepo;
    }

    @Transactional
    public CardResponseDto create(
            Long wsId,
            CardRequestDto dto,
            User user
    ) {
        System.out.println("서비스 호출");

        var ws = wsRepo.findById(wsId)
                .orElseThrow(() -> new IllegalArgumentException("워크스페이스가 없습니다."));
        if (!ws.getOwner().getId().equals(user.getId())) {
            throw new IllegalStateException("권한이 없습니다.");
        }

        Card c = new Card();
        c.setTitle(dto.title());
        c.setDescription(dto.description());
        c.setScheduledAt(dto.scheduledAt());
        c.setReminder(dto.reminder());
        c.setWorkspace(ws);

        int maxPos = cardRepo.findMaxPosition(wsId);
        c.setPosition(maxPos + 1);
        cardRepo.save(c);

        return new CardResponseDto(c);
    }

    @Transactional(readOnly = true)
    public List<CardResponseDto> listBoard(Long wsId, User user) {
        return cardRepo.findByWorkspaceIdOrderByPositionAsc(wsId)
                .stream()
                .map(CardResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CalendarCountDto> listCalendar(
            Long wsId,
            User user,
            int year,
            int month
    ) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);

        var cards = cardRepo.findByWorkspaceIdAndScheduledAtBetween(
                wsId,
                start.atStartOfDay(),
                end.plusDays(1).atStartOfDay()
        );

        Map<LocalDate, List<CardResponseDto>> grouped = cards.stream()
                .map(CardResponseDto::new)
                .collect(Collectors.groupingBy(d -> d.scheduledAt().toLocalDate()));

        return grouped.entrySet().stream()
                .map(e -> new CalendarCountDto(e.getKey(), e.getValue().size(), e.getValue()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void reorder(Long wsId, List<Long> orderedIds, User user) {
        for (int i = 0; i < orderedIds.size(); i++) {
            Long cardId = orderedIds.get(i);
            final int newPos = i + 1;
            cardRepo.findById(cardId).ifPresent(c -> c.setPosition(newPos));
        }
    }
}