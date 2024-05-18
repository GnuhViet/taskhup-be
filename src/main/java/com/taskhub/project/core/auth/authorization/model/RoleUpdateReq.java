package com.taskhub.project.core.auth.authorization.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RoleUpdateReq {
    @NotBlank(message = "id is mandatory")
    private String id;
    // @NotNull(message = "Name are mandatory")
    private String name;
    // @NotNull(message = "Actions are mandatory")
    private List<String> actionCode;
}
