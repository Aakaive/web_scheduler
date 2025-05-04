package com.aaka.web_scheduler.domain.workspace.repository;

import com.aaka.web_scheduler.domain.workspace.entity.Workspace;
import com.aaka.web_scheduler.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    List<Workspace> findAllByOwner(User owner);

    long countByOwner(User owner);

}
