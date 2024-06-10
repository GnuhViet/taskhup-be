package com.taskhub.project.core.board.resources.api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BoardBgUpdateReq {
    @NotBlank(message = "id is required")
    private String id;

    private String color;
}
