package com.taskhub.project.core.board.resources.api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BoardCreateReq {
    @NotBlank(message = "title is required")
    private String title;
    @NotBlank(message = "background is required")
    private String background;
    @NotBlank(message = "workspaceId is required")
    private String workspaceId;
}
