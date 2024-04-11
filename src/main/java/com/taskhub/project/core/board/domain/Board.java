package com.taskhub.project.core.board.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "board")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String title;
    private String description;
    private String type;

    private String createBy;
    private String createDate;

    private String memberIds;

    @Column(columnDefinition = "TEXT")
    private String columnOrderIds;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "board"
    )
    private List<BoardColumn> columns;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private WorkSpace workspace;
}
