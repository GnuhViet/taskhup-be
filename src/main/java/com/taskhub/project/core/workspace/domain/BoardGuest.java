package com.taskhub.project.core.workspace.domain;

import com.taskhub.project.core.board.domain.Board;
import com.taskhub.project.core.user.entities.AppUser;
import com.taskhub.project.core.auth.authorization.domain.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "board_guest")
public class BoardGuest {
    @EmbeddedId
    private BoardGuestKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private AppUser user;

    @ManyToOne
    @MapsId("boardId")
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    private LocalDateTime joinDate;

    private String inviteStatus;
}
