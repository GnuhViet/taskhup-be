package com.taskhub.project.core.board.repo;

import com.taskhub.project.core.board.domain.BoardCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardCardRepo extends JpaRepository<BoardCard, String> {


    @Query(value = """
        select
            bc.id as id,
            bc.template_id as templateId,
            bc.title as title,
            bc.board_column_id as columnId,
            bcl.title as columnName,
            -- members
            bc.card_label_values as selectedLabelsIdRaw,
            -- isWatchCard
            bc.from_date as fromDate,
            bc.deadline_date as deadlineDate,
            bc.working_status as workingStatus,
            bc.description as description,
            bc.check_list_value as checkListsRaw,
            -- custom fields
            bc.custom_field_value as selectedFieldsValueRaw
            -- attachments
            -- comments
            -- activity history
        from board_card bc
            join board_column bcl on bc.board_column_id = bcl.id
        where bc.id = :boardCardId
    """, nativeQuery = true)
    Optional<BoardCard.BoardCardDetail> getCardDetails(String boardCardId);


    @Query(value = """
        select * from board_card where id = :boardCardId
    """, nativeQuery = true)
    Optional<BoardCard> findById(String boardCardId);
}
