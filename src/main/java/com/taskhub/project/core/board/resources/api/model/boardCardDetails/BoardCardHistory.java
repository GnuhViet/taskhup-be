package com.taskhub.project.core.board.resources.api.model.boardCardDetails;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardCardHistory {
    private String id;
    private String type;
    private String from;
    private String to;
    private LocalDateTime date;
}
