package com.taskhub.project.core.board.resources.api.model;

import lombok.Data;

@Data
public class GetAllBoardResp {
    private String id;
    private String title;
    private String description;
}
