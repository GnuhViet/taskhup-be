package com.taskhub.project.core.board.repo;

import com.taskhub.project.core.board.domain.BoardCardMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardCardMemberRepo extends JpaRepository<BoardCardMember, String> {

    @Query(value = """
        select
            au.id as id,
            au.full_name as fullName,
            au.username as username,
            fi.url as avatarUrl
        from board_card_member bcm
            join app_user au on bcm.user_id = au.id
            join file_info fi on au.avatar = fi.id
        where bcm.card_id = :cardId
    """, nativeQuery = true)
    List<BoardCardMember.BoardCardMemberDetail> findByCardId(String cardId);

    @Query(value = """
        select
            *
        from board_card_member bcm
        where bcm.card_id = :cardId
    """, nativeQuery = true)
    List<BoardCardMember> findObjByCardId(String cardId);
}
