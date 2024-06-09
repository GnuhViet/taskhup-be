package com.taskhub.project.core.board.resources.api.model.boardCardApiModel;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EditCommentContentReq {
    @NotBlank(message = "Id is required")
    private String id;
    @NotBlank(message = "Content is required")
    private String content;
}
