package com.taskhub.project.core.board.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "board_card")
public class BoardCard {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String title;
    private String description;
    private String cover;
    private String memberIds;
    private String comments;
    private String attachments;

    @Column(columnDefinition = "TEXT")
    private String CardLabelValues;

    // map json
    // fieldId -> value
    // 1 -> true,false,true,true ( dropdown )
    // 2 -> 3 (dropdown-option-value-order-selecter)
    @Column(columnDefinition = "TEXT")
    private String CustomFieldValue;

    private String templateId;

    @ManyToOne
    @JoinColumn(name = "board_column_id")
    private BoardColumn boardColumn;
}
