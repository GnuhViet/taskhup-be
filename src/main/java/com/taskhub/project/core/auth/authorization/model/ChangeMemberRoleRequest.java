package com.taskhub.project.core.auth.authorization.model;

import lombok.Data;

@Data
public class ChangeMemberRoleRequest {
    private String memberId;
    private String roleId;
}
