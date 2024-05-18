package com.taskhub.project.core.auth.authorization.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RoleCreateReq {
    @NotBlank(message = "Role name is mandatory")
    private String name;
    @NotBlank(message = "Color is mandatory")
    private String color;
    @NotNull(message = "Actions are mandatory")
    private List<String> actionCode;
}
