package com.taskhub.project.core.workspace.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class WorkSpaceMemberKey implements Serializable {
    @Column(name = "user_id")
    private String userId;
    @Column(name = "workspace_id")
    private String workspaceId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkSpaceMemberKey that = (WorkSpaceMemberKey) o;
        return Objects.equals(userId, that.userId) && Objects.equals(workspaceId, that.workspaceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, workspaceId);
    }
}
