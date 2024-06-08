package com.taskhub.project.core.board.resources.api.model.boardCardApiModel;

import com.taskhub.project.core.board.resources.api.model.boardCardDetails.BoardCardCheckList;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class UpdateCheckListReq {
    private List<BoardCardCheckList> checkListValue;

    @NotBlank(message = "Board card id is required")
    private String boardCardId;
}
