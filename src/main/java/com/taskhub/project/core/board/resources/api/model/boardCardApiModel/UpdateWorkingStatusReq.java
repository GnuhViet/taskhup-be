package com.taskhub.project.core.board.resources.api.model.boardCardApiModel;

import lombok.Data;

@Data
public class UpdateWorkingStatusReq {
    private Boolean workingStatus;
    private String boardCardId;
}
