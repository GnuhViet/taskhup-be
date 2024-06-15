package com.taskhub.project.core.board.resources.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ActionDeleteReq {
    @NotBlank(message = "id is required")
    private String id;

    @Pattern(regexp = "^(DELETE_CARD|DELETE_COLUMN)$", message = "Type must be DELETE_CARD or DELETE_COLUMN")
    @NotBlank(message = "type is required")
    private String type;

    @NotBlank(message = "type is required")
    @Pattern(regexp = "^(ACCEPT|DECLINE)$", message = "Type must be ACCEPT or DECLINE")
    private String actionType;
}
