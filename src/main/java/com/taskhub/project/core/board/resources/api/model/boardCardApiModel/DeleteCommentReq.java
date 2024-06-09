package com.taskhub.project.core.board.resources.api.model.boardCardApiModel;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteCommentReq {
    @NotBlank(message = "Id is required")
    private String id;
}
