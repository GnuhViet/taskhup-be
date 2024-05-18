package com.taskhub.project.core.auth.authorization.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String color;
    private String workspaceId;
    private String createBy;
    private LocalDateTime createDate;
    private String updateBy;
    private LocalDateTime updateDate;

    @Column(columnDefinition = "TEXT")
    private String actionCode;

    public interface ActionOnly {
        String getActionCode();
    }

    public interface RoleWithMember {
        String getId();
        String getName();
        String getColor();
        String getCreateBy();
        LocalDateTime getCreateDate();
        String getActionCode();
        String getMember();
    }

    public interface RoleMemberList {
        String getId();
        String getMember();
    }
}
