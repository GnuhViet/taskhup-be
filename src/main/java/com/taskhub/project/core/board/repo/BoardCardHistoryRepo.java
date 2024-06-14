package com.taskhub.project.core.board.repo;

import com.taskhub.project.core.board.domain.BoardCardHistory;
import com.taskhub.project.core.board.resources.api.model.ReviewRequest;
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



    @Query(value = """
        select
            bch.id as id,
            bch.board_card_id as boardCardId,
            bc.title as boardCardName,
            bch.type as type,
            bch.to_data as toData,
            bch.created_at as actionDate,
            bch.created_by as userId,
            au.id as user_id,
            au.username as userName,
            au.full_name as userFullName,
            fi.url as userAvatar
        from board_card_history bch
            join board_card bc on bch.board_card_id = bc.id
            join app_user au on bch.created_by = au.id
            left join file_info fi on au.avatar = fi.id
        where bch.board_card_id in (
            select bcw1.card_id
            from board_card_watch bcw1
            where bcw1.user_id = :userId
        )
        and (
            IF(:isUnreadOnly = true,
                bch.id not in (
                    select unr.history_id from user_notification_read unr
                    where unr.user_id = :userId
                    and unr.history_id = bch.id
                ),
                true
            )
        )
        order by bch.created_at desc
    """, nativeQuery = true)
    List<BoardCardHistory.Notification> findNotificationByUserId(String userId, boolean isUnreadOnly);



    @Query(value = """
        select
            bch.id as id,
            bcard.id as boardCardId,
            bcard.title as boardCardName,
            bch.created_at as createdAt,
            bch.created_by as userId,
            au.full_name as userFullName,
            fi.url as userAvatar,
            au.username as username
        from board_card bcard
            join board_column bcol on bcard.board_column_id = bcol.id
            join board b on bcol.board_id = b.id
            join board_card_history bch on bcard.id = bch.board_card_id
            join app_user au on bch.created_by = au.id
            left join file_info fi on au.avatar = fi.id
        where bcard.working_status = '2'
        and b.id = :boardId
    """, nativeQuery = true)
    List<BoardCardHistory.Review> getReviewRequest(String boardId);


    @Query(value = """
        (
        select
            bch.id as id,
            bcard.id as refId,
            bcard.title as refName,
            bch.created_at as createdAt,
            bch.type as 'type',
            bch.created_by as userId,
            au.full_name as userFullName,
            fi.url as userAvatar,
            au.username as username
        from board_card_history bch
            join board_card bcard on bch.board_card_id = bcard.id
            join board_column bcol on bcard.board_column_id = bcol.id
            join board b on bcol.board_id = b.id
            join app_user au on bch.created_by = au.id
            left join file_info fi on au.avatar = fi.id
        where
            bch.type = 'DELETE_CARD'
            and b.id = :boardId
        )
        union all
        (
        select
            bch.id as id,
            bcol.id as refId,
            bcol.title as refName,
            bch.created_at as createdAt,
            bch.type as 'type',
            bch.created_by as userId,
            au.full_name as userFullName,
            fi.url as userAvatar,
            au.username as username
        from board_column_history bch
            join board_column bcol on bch.column_id = bcol.id
            join board b on bcol.board_id = b.id
            join app_user au on bch.created_by = au.id
            left join file_info fi on au.avatar = fi.id
        where
            bch.type = 'DELETE_COLUMN'
            and b.id = :boardId
        )
    """, nativeQuery = true)
    List<BoardCardHistory.Delete> getCardDeleteRequest(String boardId);
}
