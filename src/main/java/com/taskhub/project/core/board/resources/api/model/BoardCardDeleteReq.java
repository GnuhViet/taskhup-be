package com.taskhub.project.core.board.resources.api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BoardCardDeleteReq {
    @NotBlank(message = "cardId is required")
    private String cardId;
}
