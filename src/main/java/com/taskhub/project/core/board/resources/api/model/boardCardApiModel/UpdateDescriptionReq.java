package com.taskhub.project.core.board.resources.api.model.boardCardApiModel;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateDescriptionReq {
    @NotBlank(message = "Description is required")
    private String description;
    @NotBlank(message = "Board card id is required")
    private String boardCardId;
}
