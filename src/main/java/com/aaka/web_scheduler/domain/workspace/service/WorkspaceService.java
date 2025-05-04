package com.aaka.web_scheduler.domain.workspace.service;

import com.aaka.web_scheduler.domain.user.entity.User;
import com.aaka.web_scheduler.domain.workspace.dto.WorkspaceResponseDto;
import com.aaka.web_scheduler.domain.workspace.entity.Workspace;
import com.aaka.web_scheduler.domain.workspace.repository.WorkspaceRepository;
import com.aaka.web_scheduler.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkspaceService {
    private final WorkspaceRepository repo;
    private final WorkspaceRepository workspaceRepository;

    @Value("${workspace.max-per-user}")
    private int maxPerUser;

    public Workspace create(String name, User owner) {
        long existing = repo.countByOwner(owner);
        if (existing >= maxPerUser) {
            throw new IllegalStateException("최대 " + maxPerUser + "개까지 생성할 수 있습니다.");
        }
        Workspace w = new Workspace();
        w.setName(name);
        w.setOwner(owner);
        return repo.save(w);
    }

    public List<WorkspaceResponseDto> listByOwner(User owner) {
        List<Workspace> workspaces = workspaceRepository.findAllByOwner(owner);

        return workspaces.stream().map(WorkspaceResponseDto::new).toList();
    }

    @Transactional
    public WorkspaceResponseDto rename(Long wsId, String newName, User owner) {
        Workspace ws = repo.findById(wsId)
                .orElseThrow(() -> new ResourceNotFoundException("워크스페이스가 없습니다."));
        if (!ws.getOwner().getId().equals(owner.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }

        ws.setName(newName);

        return new WorkspaceResponseDto(ws);
    }

    @Transactional
    public void delete(Long wsId, User owner) {
        Workspace ws = repo.findById(wsId)
                .orElseThrow(() -> new ResourceNotFoundException("워크스페이스가 없습니다."));
        if (!ws.getOwner().getId().equals(owner.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
        repo.delete(ws);
    }


}
