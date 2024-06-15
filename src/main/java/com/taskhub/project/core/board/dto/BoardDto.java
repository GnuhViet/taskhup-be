package com.taskhub.project.core.board.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BoardDto {
    private String id;
    private String title;
    private String shortDescription;
    private String columnOrderIds;
    private String color;
    private Boolean isNeedReview;
    private Boolean isOnlyMemberEdit;
    private List<BoardColumnDto> columns;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
