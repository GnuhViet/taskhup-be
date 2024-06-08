package com.taskhub.project.core.board.resources.api.model.boardCardApiModel;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class SelectCardLabelRequest {
    // luu theo dang json string
    // labelId, labelId,...
    private List<String> boardCardLabelValue;
    @NotBlank(message = "Board card id is required")
    private String boardCardId;
}
