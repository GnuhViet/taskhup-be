package com.taskhub.project.core.board.repo;

import com.taskhub.project.core.board.domain.BoardCardHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardCardHistoryRepo extends JpaRepository<BoardCardHistory, String> {

    @Query(value = """
        select *
        from board_card_history bch
        where bch.board_card_id = :cardId
    """, nativeQuery = true)
    List<BoardCardHistory> findByCardId(String cardId);
}
