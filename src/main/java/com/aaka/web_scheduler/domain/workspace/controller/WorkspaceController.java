package com.aaka.web_scheduler.domain.workspace.controller;

import com.aaka.web_scheduler.domain.user.entity.User;
import com.aaka.web_scheduler.domain.workspace.dto.RenameWorkspaceRequestDto;
import com.aaka.web_scheduler.domain.workspace.dto.WorkspaceResponseDto;
import com.aaka.web_scheduler.domain.workspace.entity.Workspace;
import com.aaka.web_scheduler.domain.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService service;

    @PostMapping
    public ResponseEntity<Workspace> create(@RequestBody Map<String, String> body) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Workspace ws = service.create(body.get("name"), user);
        return ResponseEntity.status(HttpStatus.CREATED).body(ws);
    }

//    @GetMapping
//    public List<Workspace> list() {
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        return service.listByOwner(user);
//    }

    @GetMapping
    public ResponseEntity<List<WorkspaceResponseDto>> listByOwner() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(service.listByOwner(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkspaceResponseDto> update(@PathVariable Long id, @RequestBody RenameWorkspaceRequestDto renameWorkspaceRequestDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(service.rename(id, renameWorkspaceRequestDto.getNewName(), user));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        service.delete(id, user);
    }

}
