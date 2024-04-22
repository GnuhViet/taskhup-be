package com.taskhub.project.core.board.domain;

import com.taskhub.project.core.user.entities.AppUser;
import com.taskhub.project.core.user.entities.Role;
import com.taskhub.project.core.workspace.domain.WorkSpace;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "workspace_member")
public class WorkSpaceMember {
    @EmbeddedId
    private WorkSpaceMemberKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private AppUser user;

    @ManyToOne
    @MapsId("workspaceId")
    @JoinColumn(name = "workspace_id")
    private WorkSpace workspace;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    private LocalDateTime joinDate;
}
