package com.taskhub.project.core.board.resources.api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BoardInfoUpdateReq {
    @NotBlank(message = "id is required")
    private String id;
    @NotBlank(message = "title is required")
    private String title;
    private String shortDescription;
    private String description;
}
