package com.taskhub.project.core.workspace.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class BoardGuestKey implements Serializable {
    @Column(name = "user_id")
    private String userId;
    @Column(name = "board_id")
    private String boardId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardGuestKey that = (BoardGuestKey) o;
        return Objects.equals(userId, that.userId) && Objects.equals(boardId, that.boardId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, boardId);
    }
}
