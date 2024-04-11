package com.taskhub.project.core.board.dto;

import lombok.Data;

import java.util.List;

@Data
public class BoardColumnDto {
    private String id;
    private String boardId;
    private String title;
    private String cardOrderIds;
    private List<BoardCardDto> cards;
}
