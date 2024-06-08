package com.taskhub.project.core.board.repo;

import com.taskhub.project.core.board.domain.BoardCardComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardCardCommentsRepo extends JpaRepository<BoardCardComments, String> {

    @Query(value = """
        select *
        from board_card_comments bcm
        where bcm.board_card_id = :cardId
    """, nativeQuery = true)
    List<BoardCardComments> findByCardId(String cardId);
}
