package com.taskhub.project.core.board.repo;

import com.taskhub.project.core.board.domain.BoardGuest;
import com.taskhub.project.core.board.domain.BoardGuestKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BoardGuestRepo extends JpaRepository<BoardGuest, BoardGuestKey> {
    @Query(value = """
        select CASE WHEN COUNT(bg.id) > 0 THEN TRUE ELSE FALSE END
        from BoardGuest bg
        where bg.id.boardId = :boardId and bg.id.userId = :userId
    """)
    boolean hasGuest(String boardId, String userId);
}
