package com.taskhub.project.core.workspace.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WorkSpaceCreateReq {
    @NotBlank(message = "Field is mandatory")
    private String title;

    @NotBlank(message = "Field is mandatory")
    private String type;

    @NotBlank(message = "Field is mandatory")
    private String description;
}
