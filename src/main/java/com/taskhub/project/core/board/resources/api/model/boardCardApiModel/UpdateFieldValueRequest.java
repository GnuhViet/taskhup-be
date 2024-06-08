package com.taskhub.project.core.board.resources.api.model.boardCardApiModel;

import com.taskhub.project.core.board.resources.api.model.boardCardDetails.BoardCardSelectedFields;
import lombok.Data;

@Data
public class UpdateFieldValueRequest {
    BoardCardSelectedFields customFieldValue;
    String boardCardId;
}
