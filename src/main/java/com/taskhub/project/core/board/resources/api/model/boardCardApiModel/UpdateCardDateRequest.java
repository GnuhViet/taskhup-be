package com.taskhub.project.core.board.resources.api.model.boardCardApiModel;

import lombok.Data;

@Data
public class UpdateCardDateRequest {
    private String fromDate;
    private String deadlineDate;
    private String boardCardId;
    private Integer reminder;
}
