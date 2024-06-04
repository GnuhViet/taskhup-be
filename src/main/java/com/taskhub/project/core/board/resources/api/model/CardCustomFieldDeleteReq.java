package com.taskhub.project.core.board.resources.api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CardCustomFieldDeleteReq {
    @NotBlank(message = "Id is required")
    private String id;
    @NotBlank(message = "Template id is required")
    private String templateId;
}
