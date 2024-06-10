package com.taskhub.project.core.board.resources.api.model.boardCardApiModel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RemoveCoverRequest {
    @NotBlank(message = "boardCardId is required")
    private String boardCardId;
}
