package com.taskhub.project.board.resources.websocket.model.BoardSocket;

import lombok.Data;

@Data
public class BoardColumnCreateResp {
    private String id;
    private String boardId;
    private String title;
}
