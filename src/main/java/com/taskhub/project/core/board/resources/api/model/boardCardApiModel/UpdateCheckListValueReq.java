package com.taskhub.project.core.board.resources.api.model.boardCardApiModel;

import com.taskhub.project.core.board.resources.api.model.boardCardDetails.BoardCardCheckList;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UpdateCheckListValueReq {
    @NotBlank(message = "Check list id is required")
    private String id;

    @NotNull(message = "Check list value is required")
    private Boolean checked;

    @NotBlank(message = "Board card id is required")
    private String boardCardId;
}
