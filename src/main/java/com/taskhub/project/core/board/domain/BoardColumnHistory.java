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
@Table(name = "board_column_history")
public class BoardColumnHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String columnId;
    private String type;
    private String fromData;
    private String toData;
    private LocalDateTime createdAt;
    private String createdBy;
}
