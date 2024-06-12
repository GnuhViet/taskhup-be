package com.taskhub.project.core.board.resources.api.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardInfoResp {
    private String id;
    private String title;
    private String shortDescription;
    private String description;
    private String color;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
