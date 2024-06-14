package com.taskhub.project.core.board.resources.api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ManageInfoReq {
    @NotBlank(message = "boardId is required")
    private String boardId;
}
