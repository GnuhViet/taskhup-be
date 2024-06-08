package com.taskhub.project.core.board.domain;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "board_card_member")
public class BoardCardMember {
    @EmbeddedId
    private BoardCardMemberKey id;

    public interface BoardCardMemberDetail {
        String getId();
        String getFullName();
        String getUsername();
        String getAvatarUrl();
    }
}
