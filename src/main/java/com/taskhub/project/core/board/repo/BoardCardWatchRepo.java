package com.taskhub.project.core.board.repo;

import com.taskhub.project.core.board.domain.BoardCardWatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BoardCardWatchRepo extends JpaRepository<BoardCardWatch, String> {

    @Query(value = """
        select
            *
        from board_card_watch bcw
        where bcw.card_id = :cardId and bcw.user_id = :userId
    """, nativeQuery = true)
    Optional<BoardCardWatch> findByCardIdAndUserId(String cardId, String userId);


    @Query(value = """
        select
            *
        from board_card_watch bcw
        where bcw.card_id = :cardId
    """, nativeQuery = true)
    Optional<List<BoardCardWatch>> findByCardId(String cardId);
}
