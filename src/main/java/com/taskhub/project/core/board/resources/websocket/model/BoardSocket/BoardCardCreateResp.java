package com.taskhub.project.core.board.resources.websocket.model.BoardSocket;

import lombok.Data;

@Data
public class BoardCardCreateResp {
    private String id;
    private String boardId;
    private String columnId;
    private String title;
}
