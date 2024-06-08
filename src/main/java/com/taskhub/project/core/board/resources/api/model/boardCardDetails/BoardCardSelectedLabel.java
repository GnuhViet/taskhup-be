package com.taskhub.project.core.board.resources.api.model.boardCardDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardCardSelectedLabel {
    private String id;
    private String title;
    private String colorCode;
    private String createDate;
}
