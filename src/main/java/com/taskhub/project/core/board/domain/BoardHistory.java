package com.taskhub.project.core.board.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "board-history")
public class BoardHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String action;

    private String deleteInfo;
}
