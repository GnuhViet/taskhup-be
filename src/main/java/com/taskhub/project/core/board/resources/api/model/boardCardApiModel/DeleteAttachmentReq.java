package com.taskhub.project.core.board.resources.api.model.boardCardApiModel;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DeleteAttachmentReq {
    private String attachmentId;
}
