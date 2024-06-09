package com.taskhub.project.core.board.resources.api.model.boardCardDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardCardAttachment {
    private String id;
    private String type;
    private String refId;
    private String fileId;
    private String uploadBy;
    private String uploadAt;
    private String displayName;

    private Long fileSize;
    private String originFileName;
    private String format;
    private String downloadUrl;
    private String resourceType;
}
