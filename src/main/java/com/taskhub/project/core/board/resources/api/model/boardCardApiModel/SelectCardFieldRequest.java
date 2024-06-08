package com.taskhub.project.core.board.resources.api.model.boardCardApiModel;

import com.taskhub.project.core.board.resources.api.model.boardCardDetails.BoardCardSelectedFields;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class SelectCardFieldRequest {
    private List<BoardCardSelectedFields> customFieldValue;
    @NotBlank(message = "Board card id is required")
    private String boardCardId;
}
