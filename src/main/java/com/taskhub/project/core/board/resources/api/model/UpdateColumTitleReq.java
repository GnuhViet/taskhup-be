package com.taskhub.project.core.board.resources.api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateColumTitleReq {
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Column id is required")
    private String columnId;
}
