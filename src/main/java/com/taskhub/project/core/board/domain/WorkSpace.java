package com.taskhub.project.core.board.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "work_space")
public class WorkSpace {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String title;

    private String createBy;
    private String createDate;

    private String memberIds;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "workspace"
    )
    private List<Board> boards;
}
