package com.taskhub.project.core.board.repo;

import com.taskhub.project.core.workspace.domain.BoardStar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BoardStarRepo extends JpaRepository<BoardStar, String> {

    @Query(value = """
            SELECT * FROM `board-star` bs
            WHERE bs.board_id = :boardId AND bs.user_id = :userId
            LIMIT 1
            """, nativeQuery = true)
    BoardStar findByBoardIdAndUserId(String boardId, String userId);
}
