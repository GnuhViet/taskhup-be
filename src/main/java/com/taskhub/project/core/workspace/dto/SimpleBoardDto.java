package com.taskhub.project.core.workspace.dto;

import lombok.Data;

@Data
public class SimpleBoardDto {
    private String id;
    private String title;
    private String shortDescription;
    private String color;
    private Boolean isStarred;
}
