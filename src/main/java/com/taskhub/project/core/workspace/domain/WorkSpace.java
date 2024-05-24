package com.taskhub.project.core.workspace.domain;

import com.taskhub.project.core.board.domain.Board;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
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
    @Column(columnDefinition = "TEXT")
    private String description;

    private String createBy;
    private LocalDateTime createDate;

    private String ownerId;

    private String website;

    private String avatarId;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "workspace"
    )
    private List<Board> boards;

    public interface UserWorkSpace {
        String getId();
        String getTitle();
        int getUserCount();
        String getType();
    }

    public interface IdOnly {
        String getId();
    }

    public interface WorkSpaceInfo {
        String getId();
        String getTitle();
        String getDescription();
        String getOwnerName();
        String getWebsite();
        String getAvatarUrl();
    }
}
