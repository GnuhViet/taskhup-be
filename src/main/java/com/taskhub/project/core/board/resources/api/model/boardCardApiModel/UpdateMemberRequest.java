package com.taskhub.project.core.board.resources.api.model.boardCardApiModel;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class UpdateMemberRequest {
    private List<String> members;

    @NotBlank(message = "Board card id is required")
    private String boardCardId;
}
