package com.taskhub.project.core.board.resources.api.model.boardCardApiModel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class UpdateCardCoverReq {
    @NotNull(message = "file is required")
    private MultipartFile file;
    @NotBlank(message = "boardCardId is required")
    private String boardCardId;
}
