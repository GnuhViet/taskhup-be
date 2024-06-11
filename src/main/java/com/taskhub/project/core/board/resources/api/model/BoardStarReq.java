package com.taskhub.project.core.board.resources.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BoardStarReq {
    @NotBlank(message = "Board id is required")
    private String boardId;
    @NotNull(message = "Starred status is required")
    private Boolean isStarred;
}
