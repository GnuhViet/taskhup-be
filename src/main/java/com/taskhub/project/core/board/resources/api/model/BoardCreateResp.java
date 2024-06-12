package com.taskhub.project.core.board.resources.api.model;

import lombok.Data;

@Data
public class BoardCreateResp {
    private String id;
    private String title;
    private String color;
    private String workspaceId;
}
