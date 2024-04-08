package com.taskhub.project.board.resources.websocket.model.BoardSocket;

import lombok.Data;

@Data
public class BoardCardCreateResp {
    private String id;
    private String boardColumnId;
    private String title;
}
