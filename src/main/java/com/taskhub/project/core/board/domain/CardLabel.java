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
@Table(name = "card_label")
public class CardLabel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;

    private String colorCode;

    private LocalDateTime createDate;

    private String templateId; // belong to which template
}
