package com.taskhub.project.core.board.repo;

import com.taskhub.project.core.board.domain.BoardCardComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardCardCommentsRepo extends JpaRepository<BoardCardComments, String> {

    @Query(value = """
        select
            bcm.id as id,
            bcm.content as content,
            bcm.create_at as createAt,
            bcm.create_by as createBy,
            au.full_name as fullName,
            au.username as username,
            fi.url as avatarUrl
        from board_card_comments bcm
            join app_user au on bcm.create_by = au.id
            left join file_info fi on au.avatar = fi.id
        where bcm.board_card_id = :cardId
        order by bcm.create_at
    """, nativeQuery = true)
    List<BoardCardComments.BoardCardCommentDetail> findByCardId(String cardId);
}
