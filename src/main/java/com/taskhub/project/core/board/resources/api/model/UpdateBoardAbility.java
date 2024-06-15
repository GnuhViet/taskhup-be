package com.taskhub.project.core.board.resources.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateBoardAbility {
    @NotBlank(message = "boardId is required")
    private String boardId;

    @NotNull(message = "isOnlyMemberEdit is required")
    private Boolean isOnlyMemberEdit;

    @NotNull(message = "isNeedReview is required")
    private Boolean isNeedReview;
}
