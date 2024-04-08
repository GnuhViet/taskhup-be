package com.taskhub.project.board.dto;

import lombok.Data;

@Data
public class BoardCardDto {
    private String id;
    private String boardId;
    private String columnId;
    private String title;
    private String description;
    private String cover;
    private String memberIds;
    private String comments;
    private String attachments;
}
