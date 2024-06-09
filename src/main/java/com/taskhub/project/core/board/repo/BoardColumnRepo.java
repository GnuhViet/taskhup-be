package com.taskhub.project.core.board.repo;

import com.taskhub.project.core.board.domain.BoardColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardColumnRepo extends JpaRepository<BoardColumn, String> {

    @Query(value = """
        select * from board_column where board_id = :boardId
    """, nativeQuery = true)
    List<BoardColumn> findByBoardId(String boardId);
}
