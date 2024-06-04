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
@Table(name = "card_custom_field")
public class CardCustomField {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String type;
    // text, number, date, dropdown, checkbox

    private String options;
        // for dropdown type (color-value,color-value,color-value)

    private String title;

    private LocalDateTime createDate;

    private String templateId; // belong to which template
}
