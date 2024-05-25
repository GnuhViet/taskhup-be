package com.taskhub.project.core.workspace.domain;

import com.taskhub.project.core.user.entities.AppUser;
import com.taskhub.project.core.auth.authorization.domain.Role;
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

    private String inviteStatus;

    public interface WorkspaceMemberDetails {
        String getRoleId();
        String getWorkspaceId();
        String getUserId();
        String getUserName();
        String getFullName();
        LocalDateTime getJoinDate();
        String getAvatarUrl();
        String getStatus();
    }
}
