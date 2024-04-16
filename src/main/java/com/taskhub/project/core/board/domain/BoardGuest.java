package com.taskhub.project.core.board.domain;

import com.taskhub.project.core.user.entities.AppUser;
import com.taskhub.project.core.user.entities.Role;
import jakarta.persistence.*;

import java.util.Date;

@Entity(name = "board_guest")
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

    private Date joinDate;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
}
