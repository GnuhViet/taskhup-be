package com.taskhub.project.board.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "board_column")
public class BoardColumn {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String cardOrderIds;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "boardColumn"
    )
    private List<BoardCard> boardCards;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;
}
