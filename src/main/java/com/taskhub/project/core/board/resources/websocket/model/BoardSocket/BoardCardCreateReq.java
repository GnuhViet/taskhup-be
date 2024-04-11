package com.taskhub.project.core.board.resources.websocket.model.BoardSocket;

import lombok.Data;

@Data
public class BoardCardCreateReq {
    private String boardColumnId;
    private String title;
}
