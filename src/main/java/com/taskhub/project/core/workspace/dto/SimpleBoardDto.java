package com.taskhub.project.core.workspace.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SimpleBoardDto {
    private String id;
    private String title;
    private String shortDescription;
    private String color;
    private Boolean isStarred;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
