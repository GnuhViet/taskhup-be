package com.taskhub.project.core.board.resources.api.model.boardCardDetails;

import com.taskhub.project.core.file.domain.FileInfo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BoardCardComment {
    private String id;
    private String content;
    private LocalDateTime createAt;
    private BoardCardMemberSimple member;
    private List<FileInfo> attachments;
}
