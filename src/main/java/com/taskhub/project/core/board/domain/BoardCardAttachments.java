package com.taskhub.project.core.board.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "board_card_attachments")
public class BoardCardAttachments {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String type;

    private String refId;

    private String fileId;

    private String uploadBy;

    private LocalDateTime uploadAt;

    private String displayName;

    public interface BoardCardAttachmentsInfo {
        String getId();
        String getType();
        String getRefId();
        String getFileId();
        String getUploadBy();
        LocalDateTime getUploadAt();
        String getDisplayName();

        Long getFileSize();
        String getOriginFileName();
        String getFormat();
        String getDownloadUrl();
        String getResourceType();
    }
}
