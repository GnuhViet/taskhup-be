package com.taskhub.project.core.board.resources.api.model.boardCardDetails;

import com.taskhub.project.core.file.domain.FileInfo;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BoardCardComment {
    private String id;
    private String content;
    private LocalDateTime createAt;
    private String createBy;
    private String fullName;
    private String username;
    private String avatarUrl;
    private Boolean editable;
    private List<BoardCardAttachment> attachments;
}
