package com.taskhub.project.core.board.resources.api.model;

import lombok.Data;

@Data
public class BoardCardTemplateCreateRequest {
    private String boardId;
    private String title;
}
