package com.taskhub.project.core.board.repo;

import com.taskhub.project.core.board.domain.BoardCardHistory;
import org.springframework.data.domain.Pageable;
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


    @Query(value = """
        select
            bch.id as id,
            bch.type as type,
            bch.to_data as toData,
            bch.created_at as actionDate,
            bch.created_by as userId,
            au.id as user_id,
            au.username as userName,
            au.full_name as userFullName,
            fi.url as userAvatar
        from board_card_history bch
            join app_user au on bch.created_by = au.id
            left join file_info fi on au.avatar = fi.id
        where bch.board_card_id = :cardId
        order by bch.created_at desc
    """, nativeQuery = true)
    List<BoardCardHistory.Details> findDetailsByCardId(String cardId, Pageable pageable);

    @Query(value = """
        select
            bch.id as id,
            bch.type as type,
            bch.to_data as toData,
            bch.created_at as actionDate,
            bch.created_by as userId,
            au.id as user_id,
            au.username as userName,
            au.full_name as userFullName,
            fi.url as userAvatar
        from board_card_history bch
            join app_user au on bch.created_by = au.id
            left join file_info fi on au.avatar = fi.id
        where bch.board_card_id = :cardId
        order by bch.created_at desc
    """, nativeQuery = true)
    List<BoardCardHistory.Details> findDetailsByCardId(String cardId);
}
