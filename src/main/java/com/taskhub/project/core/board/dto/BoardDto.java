package com.taskhub.project.core.board.dto;

import lombok.Data;

import java.util.List;

@Data
public class BoardDto {
    private String id;
    private String name;
    private String title;
    private String description;
    private String type;
    private String ownerIds;
    private String memberIds;
    private String columnOrderIds;
    private String color;
    private List<BoardColumnDto> columns;
}
