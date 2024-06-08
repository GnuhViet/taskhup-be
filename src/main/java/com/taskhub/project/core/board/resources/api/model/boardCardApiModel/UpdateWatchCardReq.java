package com.taskhub.project.core.board.resources.api.model.boardCardApiModel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateWatchCardReq {
    @NotBlank(message = "boardCardId is required")
    private String boardCardId;
    @NotNull(message = "isWatch is required")
    private Boolean isWatch;
}
