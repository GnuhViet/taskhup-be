package com.taskhub.project.core.board.resources.websocket.model.BoardSocket;

import com.taskhub.project.core.board.dto.BoardCardDto;
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
