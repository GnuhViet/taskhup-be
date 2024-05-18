package com.taskhub.project.core.workspace.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "board-star")
public class BoardStar {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "boardId is required")
    private String boardId;
    @NotBlank(message = "userId is required")
    private String userId;
}
