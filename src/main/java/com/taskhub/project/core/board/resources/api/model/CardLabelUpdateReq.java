package com.taskhub.project.core.board.resources.api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CardLabelUpdateReq {
    @NotBlank(message = "Id is required")
    private String id;
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Color code is required")
    private String colorCode;
    @NotBlank(message = "Template id is required")
    private String templateId;
}
