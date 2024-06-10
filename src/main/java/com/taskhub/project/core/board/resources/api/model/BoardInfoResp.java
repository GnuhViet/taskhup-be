package com.taskhub.project.core.board.resources.api.model;

import lombok.Data;

@Data
public class BoardInfoResp {
    private String id;
    private String title;
    private String shortDescription;
    private String description;
    private String color;
}
