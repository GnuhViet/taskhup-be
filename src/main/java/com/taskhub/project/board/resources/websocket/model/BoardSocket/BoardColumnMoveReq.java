package com.taskhub.project.board.resources.websocket.model.BoardSocket;

import lombok.Data;

import java.util.List;

@Data
public class BoardColumnMoveReq {
    private List<String> columnOrderIds;
}
