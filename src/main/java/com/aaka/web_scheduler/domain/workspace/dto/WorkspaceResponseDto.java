package com.aaka.web_scheduler.domain.workspace.dto;

import com.aaka.web_scheduler.domain.workspace.entity.Workspace;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkspaceResponseDto {
    private Long id;
    private String name;
    private Long userId;

    public WorkspaceResponseDto(Workspace workspace) {
        this.id = workspace.getId();
        this.name = workspace.getName();
        this.userId = workspace.getOwner().getId();
    }
}
