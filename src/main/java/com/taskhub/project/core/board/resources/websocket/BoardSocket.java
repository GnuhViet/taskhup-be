package com.taskhub.project.core.board.resources.websocket;

import com.taskhub.project.common.socket.model.SocketResponse;
import com.taskhub.project.core.board.service.BoardService;
import com.taskhub.project.core.board.resources.websocket.model.BoardSocket.*;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
@MessageMapping("/board")
public class BoardSocket {

    public static enum ACTION {
        CREATE_COLUMN,
        MOVE_COLUMN,
        DELETE_COLUMN,
        CREATE_CARD,
        MOVE_CARD,
        DELETE_CARD
    }

    private final SimpMessageSendingOperations messageTemplate;
    private final BoardService boardService;

    @MessageMapping("/{boardId}/createColumn")
    public void createColumn(
            @DestinationVariable String boardId,
            @Payload BoardColumnCreateReq req
    ) {
        var result = boardService.createColumn(boardId, req);

        var response = SocketResponse.<BoardColumnCreateResp, ACTION>builder()
                .data(result.getData())
                .action(ACTION.CREATE_COLUMN)
                .build();

        messageTemplate.convertAndSend("/topic/board/" + boardId, response);
    }

    @MessageMapping("/{boardId}/moveColumn")
    public void moveColumn(
            @DestinationVariable String boardId,
            @Payload BoardColumnMoveReq req
    ) {
        var result = boardService.moveColumn(boardId, req);

        var response = SocketResponse.<BoardColumnMoveReq, ACTION>builder()
                .data(result.getData())
                .action(ACTION.MOVE_COLUMN)
                .build();

        messageTemplate.convertAndSend("/topic/board/" + boardId, response);
    }

    @MessageMapping("/{boardId}/createCard")
    public void createCard(
            @DestinationVariable String boardId,
            @Payload BoardCardCreateReq req
            ) {
        var result = boardService.createCard(boardId, req);

        var response = SocketResponse.<BoardCardCreateResp, ACTION>builder()
                .data(result.getData())
                .action(ACTION.CREATE_CARD)
                .build();

        messageTemplate.convertAndSend("/topic/board/" + boardId, response);
    }

    @MessageMapping("/{boardId}/moveCard")
    public void moveCard(
            @DestinationVariable String boardId,
            @Payload BoardCardMoveReq req
    ) {
        var result = boardService.moveCard(boardId, req);

        var response = SocketResponse.<BoardCardMoveReq, ACTION>builder()
                .data(result.getData())
                .action(ACTION.MOVE_CARD)
                .build();

        messageTemplate.convertAndSend("/topic/board/" + boardId, response);
    }
}
