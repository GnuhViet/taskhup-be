package com.taskhub.project.core.auth.authorization.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RoleAddMemberReq {
    @NotBlank(message = "id is mandatory")
    private String id;
    @NotNull(message = "User ids are mandatory")
    private String userId;
}
