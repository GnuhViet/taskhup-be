package com.taskhub.project.core.board.resources.api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteColumnReq {
    @NotBlank(message = "Column id is required")
    private String columnId;
}
