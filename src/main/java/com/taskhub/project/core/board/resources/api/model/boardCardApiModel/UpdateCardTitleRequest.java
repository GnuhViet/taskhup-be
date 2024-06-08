package com.taskhub.project.core.board.resources.api.model.boardCardApiModel;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateCardTitleRequest {
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Board card id is required")
    private String boardCardId;
}
