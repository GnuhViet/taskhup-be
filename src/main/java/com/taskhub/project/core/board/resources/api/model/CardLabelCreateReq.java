package com.taskhub.project.core.board.resources.api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CardLabelCreateReq {
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Color code is required")
    private String colorCode;
    @NotBlank(message = "Template id is required")
    private String templateId;
}
