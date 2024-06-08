package com.taskhub.project.core.board.resources.api.model.boardCardDetails;

import lombok.Data;

@Data
public class BoardCardSelectedFields {
    private String fieldId;
    private String value; // neu la dropdown thi value la thu tu cua option
}
