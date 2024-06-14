package com.taskhub.project.core.user.entities;

import com.taskhub.project.core.board.domain.BoardCardMemberKey;
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
public class UserNotificationReadKey implements Serializable {
    @Column(name = "user_id")
    private String userId;
    @Column(name = "history_id")
    private String historyId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserNotificationReadKey that = (UserNotificationReadKey) o;
        return Objects.equals(userId, that.userId) && Objects.equals(historyId, that.historyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, historyId);
    }}
