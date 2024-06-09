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
@Table(name = "board_card_comments")
public class BoardCardComments {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createAt;

    private String createBy; // ref app_user_id

    private String boardCardId; // ref board_card_id

    public interface BoardCardCommentDetail {
        String getId();
        String getContent();
        LocalDateTime getCreateAt();
        String getCreateBy();
        String getFullName();
        String getUsername();
        String getAvatarUrl();
    }
}
