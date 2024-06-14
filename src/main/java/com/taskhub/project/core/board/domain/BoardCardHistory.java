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
@Table(name = "board_card_history")
public class BoardCardHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String boardCardId; // ref board_card_id

    private String fromData;

    private String toData;

    private String type;

    private LocalDateTime createdAt;

    private String createdBy;

    public interface Details {
        String getId();
        String getType();
        String getToData();
        LocalDateTime getActionDate();
        String getUserId();
        String getUserName();
        String getUserFullName();
        String getUserAvatar();
    }

    public interface Notification {
        String getId();
        String getBoardCardId();
        String getBoardCardName();
        String getType();
        String getToData();
        LocalDateTime getActionDate();
        Boolean getIsRead();
        String getUserId();
        String getUserName();
        String getUserFullName();
        String getUserAvatar();
    }
}
