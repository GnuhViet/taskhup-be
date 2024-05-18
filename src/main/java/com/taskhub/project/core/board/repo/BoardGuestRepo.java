package com.taskhub.project.core.board.repo;

import com.taskhub.project.core.workspace.domain.BoardGuest;
import com.taskhub.project.core.workspace.domain.BoardGuestKey;
import com.taskhub.project.core.workspace.domain.WorkSpaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BoardGuestRepo extends JpaRepository<BoardGuest, BoardGuestKey> {
    @Query(value = """
        select CASE WHEN COUNT(bg.id) > 0 THEN TRUE ELSE FALSE END
        from BoardGuest bg
        where bg.id.boardId = :boardId and bg.id.userId = :userId
    """)
    boolean hasGuest(String boardId, String userId);
}
