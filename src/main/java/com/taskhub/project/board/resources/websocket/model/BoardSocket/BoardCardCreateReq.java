package com.taskhub.project.board.resources.websocket.model.BoardSocket;

import lombok.Data;

@Data
public class BoardCardCreateReq {
    private String boardColumnId;
    private String title;
}
