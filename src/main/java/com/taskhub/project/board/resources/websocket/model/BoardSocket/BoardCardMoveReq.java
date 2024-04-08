package com.taskhub.project.board.resources.websocket.model.BoardSocket;

import com.taskhub.project.board.dto.BoardCardDto;
import lombok.Data;

import java.util.List;

@Data
public class BoardCardMoveReq {
    private String cardId;
    private String fromColumnId;
    private String toColumnId;
    private List<String> cardOrderIds;
    private BoardCardDto card;
}
