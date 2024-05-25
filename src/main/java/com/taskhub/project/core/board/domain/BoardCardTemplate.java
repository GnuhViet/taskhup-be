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
@Table(name = "board_card_template")
public class BoardCardTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;

    private String avatar;

    @Column(columnDefinition = "TEXT")
    private String CardLabelIds;

    @Column(columnDefinition = "TEXT")
    private String CardCustomFieldIds;

    private String boardId; // This is the foreign key to the board table

    private LocalDateTime createDate;

    public interface BoardTemplateDetail {
        String getId();
        String getTitle();
        String getAvatar();
        Long getUsedIn();
    }
}
