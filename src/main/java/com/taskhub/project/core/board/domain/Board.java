package com.taskhub.project.core.board.domain;

import com.taskhub.project.core.workspace.domain.WorkSpace;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "board")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;

    private String shortDescription;

    private String color;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String type;

    private String createBy;
    private String createDate;

    // @ManyToMany(fetch = FetchType.LAZY)
    // @JoinTable(
    //         name = "board_member",
    //         joinColumns = @JoinColumn(name = "board_id"),
    //         inverseJoinColumns = @JoinColumn(name = "user_id")
    // )
    // private Set<AppUser> appUsers;

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

    public interface SimpleBoard {
        String getId();
        String getTitle();
        String getShortDescription();
        String getType();
    }
}
