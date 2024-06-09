package com.taskhub.project.core.board.resources.api.model.boardCardApiModel;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCommentReq {
    @NotBlank(message = "Content is required")
    private String content;
    @NotBlank(message = "Board card id is required")
    private String boardCardId;
}
