package com.taskhub.project.core.board.resources.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ActionReviewRequest {
    @NotBlank(message = "cardId is required")
    private String cardId;

    // accept or decline
    @NotBlank(message = "type is required")
    @Pattern(regexp = "^(ACCEPT|DECLINE)$", message = "Type must be ACCEPT or DECLINE")
    private String type;
}
