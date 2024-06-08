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
}
