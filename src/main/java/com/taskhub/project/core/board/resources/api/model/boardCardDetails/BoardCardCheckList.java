package com.taskhub.project.core.board.resources.api.model.boardCardDetails;

import lombok.Data;

@Data
public class BoardCardCheckList {
    private String id;
    private String title;
    private boolean checked;
}
