package com.taskhub.project.core.board.resources.api.model.boardCardApiModel;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadAttachmentRequest {
    private String displayName;

    @Pattern(regexp = "^(CARD_ATTACH|COMMENT_ATTACH)$", message = "Type must be CARD_ATTACH or COMMENT_ATTACH")
    private String type;

    @NotNull(message = "RefId is required")
    private String refId;
}
