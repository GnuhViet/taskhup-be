package com.taskhub.project.core.workspace.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WorkSpaceUpdateInfoRequest {
    @NotBlank(message = "Title is required")
    private String title;
    private String website;
    @NotBlank(message = "Description is required")
    private String description;
}
