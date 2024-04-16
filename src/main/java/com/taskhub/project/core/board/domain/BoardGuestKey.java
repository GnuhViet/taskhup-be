package com.taskhub.project.core.board.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class BoardGuestKey implements Serializable {
    @Column(name = "user_id")
    private String userId;
    @Column(name = "board_id")
    private String boardId;
}
